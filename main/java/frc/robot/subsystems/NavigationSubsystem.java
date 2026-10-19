package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotContainer;
import frc.robot.Constants;

public class NavigationSubsystem extends Subsystem {

    public enum Estado {
        PARADO,
        ANDANDO_FRENTE,
        VERIFICANDO,
        VIRANDO,
        BECO_SEM_SAIDA,   
        FINALIZADO
    }
    private Estado estado = Estado.PARADO;
    
    private double grausAlvoGiro    = 0.0;
    private double anguloAoVirar    = 0.0; 
    // =========================================================
    //  API pública
    // =========================================================

    /** Inicia a navegação livre — chame no autonomousInit() */
    public void iniciar() {
        giroCalculado = false;
        RobotContainer.drive.resetBlocos();
        RobotContainer.drive.stopMotors();
        estado = Estado.ANDANDO_FRENTE;
        atualizarDashboard();
    }

    /** Para tudo — chame no botão STOP */
    public void parar() {
        estado = Estado.PARADO;
        RobotContainer.drive.resetBlocos();
        RobotContainer.drive.stopMotors();
    }

    public boolean isFinalizado() { return estado == Estado.FINALIZADO; }

    // =========================================================
    //  Periodic
    // =========================================================
    public void periodic() {
        atualizarDashboard();

        switch (estado) {
            case PARADO:
            case FINALIZADO:
                break;

            case ANDANDO_FRENTE:  executarAndadaFrente(); break;
            case VERIFICANDO:     executarVerificacao();  break;
            case VIRANDO:         executarGiro();         break;

            case BECO_SEM_SAIDA:
                // para os motores e pisca LED de run
                RobotContainer.drive.stopMotors();
                RobotContainer.oi.piscarLedRun();
                break;
        }
    }

    // =========================================================
    //  ANDANDO_FRENTE
    //  Anda reto no ângulo atual com correção pelas rodas traseiras.
    //  Detecta parede na frente e muda estado.
    // =========================================================
    private void executarAndadaFrente() {

        // parede na frente? para e vai verificar os lados
        if (RobotContainer.sensors.temParedeFrente()) {
            RobotContainer.drive.stopMotors();
            estado = Estado.VERIFICANDO;
            return;
        }

        // ── anda reto com correção de ângulo ──────────────────
        // mantém o mesmo ângulo que estava quando entrou nesse estado
        double anguloAlvo = grausAlvoGiro; // 0° no início, atualiza após giros
        double erroAngulo = normalizeAngle180(anguloAlvo - RobotContainer.drive.getAngulo());
        double correcao   = clamp(erroAngulo * Constants.KP_ROT, -0.2, 0.2);

        // double v = Constants.VEL_MOVE;
        double v = 0.2;

        // rodas traseiras (motor2, motor3) compensam o desvio
        RobotContainer.drive.setMotors(
             v,             // motor0 — frente esquerda
            -v,             // motor1 — frente direita
             v + correcao,  // motor2 — trás esquerda
            -(v + correcao) // motor3 — trás direita
        );

        SmartDashboard.putNumber("Nav/erroAngulo", erroAngulo);
        SmartDashboard.putNumber("Nav/correcao",   correcao);
    }

    // ── flag para capturar o alvo só uma vez ──────────────────
    private boolean giroCalculado = false;

    // =========================================================
    //  VERIFICANDO
    // =========================================================
    private void executarVerificacao() {

        if (giroCalculado) return; // já calculou — não recalcula

        // pequena pausa para o robô parar completamente
        // antes de ler os sensores laterais
        RobotContainer.drive.stopMotors();

        boolean paredeEsq = RobotContainer.sensors.temParedeEsquerda();
        boolean paredeDir = RobotContainer.sensors.temParedeDireita();

        SmartDashboard.putBoolean("Nav/ParedeEsq", paredeEsq);
        SmartDashboard.putBoolean("Nav/ParedeDir", paredeDir);

        anguloAoVirar = RobotContainer.drive.getAngulo();

        if (!paredeEsq && paredeDir) {
            grausAlvoGiro = normalizeAngle180(anguloAoVirar - 90.0);
            SmartDashboard.putString("Nav/Decisao", "Vira esquerda");

        } else if (paredeEsq && !paredeDir) {
            grausAlvoGiro = normalizeAngle180(anguloAoVirar + 90.0);
            SmartDashboard.putString("Nav/Decisao", "Vira direita");

        } else if (!paredeEsq && !paredeDir) {
            grausAlvoGiro = normalizeAngle180(anguloAoVirar + 90.0);
            SmartDashboard.putString("Nav/Decisao", "Ambos livres - direita");

        } else {
            SmartDashboard.putString("Nav/Decisao", "Beco sem saida - PARADO");
            RobotContainer.drive.stopMotors();
            RobotContainer.drive.resetBlocos();
            estado = Estado.BECO_SEM_SAIDA;
            return;
        }

        SmartDashboard.putNumber("Nav/AnguloReferencia", anguloAoVirar);
        SmartDashboard.putNumber("Nav/AnguloAlvoGiro",   grausAlvoGiro);

        giroCalculado = true;          // trava — não recalcula mais
        RobotContainer.drive.resetBlocos();
        estado = Estado.VIRANDO;
    }

    // =========================================================
    //  VIRANDO
    //  Giro implementado direto no NavigationSubsystem.
    //  Não usa girarParaAngulo() do DriveSubsystem.
    // =========================================================
    private void executarGiro() {

        double anguloAtual = RobotContainer.drive.getAngulo();
        double erro        = normalizeAngle180(grausAlvoGiro - anguloAtual);

        SmartDashboard.putNumber("Nav/AnguloAlvo",  grausAlvoGiro);
        SmartDashboard.putNumber("Nav/AnguloAtual", anguloAtual);
        SmartDashboard.putNumber("Nav/ErroGiro",    erro);

        // ── chegou no alvo ────────────────────────────────────
        if (Math.abs(erro) <= 5.0) {
            RobotContainer.drive.stopMotors();
            grausAlvoGiro = anguloAtual; // nova referência de reta
            giroCalculado = false;
            estado        = Estado.ANDANDO_FRENTE;
            return;
        }

        // ── velocidade proporcional ao erro ───────────────────
        double omega = erro * 0.005; // ganho suave

        // velocidade mínima para vencer atrito
        if (omega > 0 && omega <  0.10) omega =  0.10;
        if (omega < 0 && omega > -0.10) omega = -0.10;

        // velocidade máxima
        omega = clamp(omega, -0.25, 0.25);

        SmartDashboard.putNumber("Nav/OmegaGiro", omega);

        // giro no próprio eixo
        RobotContainer.drive.setMotors(
            omega,
            omega,
            omega,
            omega
        );
    }
    // =========================================================
    //  Utilitários
    // =========================================================
    private double normalizeAngle180(double deg) {
        while (deg >  180.0) deg -= 360.0;
        while (deg < -180.0) deg += 360.0;
        return deg;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    // =========================================================
    //  Dashboard
    // =========================================================
    private void atualizarDashboard() {
        SmartDashboard.putString("Nav/Estado",      estado.toString());
        SmartDashboard.putNumber("Nav/AnguloAlvo",  grausAlvoGiro);
        SmartDashboard.putNumber("Nav/AnguloAtual", RobotContainer.drive.getAngulo());
        SmartDashboard.putNumber("Nav/X",           RobotContainer.drive.getX());
        SmartDashboard.putNumber("Nav/Y",           RobotContainer.drive.getY());
        
    }

    @Override
    protected void initDefaultCommand() {}
}