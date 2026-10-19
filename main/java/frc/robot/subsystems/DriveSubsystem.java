package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.studica.frc.Titan;
import com.studica.frc.Servo;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTableEntry; 
import frc.robot.Constants;

public class DriveSubsystem extends Subsystem {
    // ── Odometria ─────────────────────────────────────────────
    private double odom_X = 0.0;  // posição atual em mm
    private double odom_Y = 0.0;

    private double enc0_anterior = 0.0;
    private double enc1_anterior = 0.0;

    public boolean pontoConcluido = false;
    private boolean pontoIniciado = false;
    // ── Hardware ──────────────────────────────────────────────
    private final AHRS gyro;

    private final Titan         titan;
    private final Servo         servo0, servo1;
    private final Titan.Motor   motor0, motor1, motor2, motor3;
    private final Titan.Encoder enc0,   enc1,   enc2,   enc3;

    // ── Flags dos blocos ──────────────────────────────────────
    private boolean moveIniciado   = false;
    public  boolean moveConcluido  = false;
    private boolean giroIniciado   = false;
    public  boolean giroConcluido  = false;
    private int     ciclosEstaveis = 0;

    public DriveSubsystem() {
        gyro  = new AHRS(SPI.Port.kMXP);
        titan = new Titan(Constants.TITAN_ID);

        servo0 = new Servo(Constants.SERVO_0);
        servo1 = new Servo(Constants.SERVO_1);

        motor0 = titan.getMotor(Constants.MOTOR_0);
        motor1 = titan.getMotor(Constants.MOTOR_2);
        motor2 = titan.getMotor(Constants.MOTOR_1);
        motor3 = titan.getMotor(Constants.MOTOR_3);

        enc0 = titan.getEncoder(Constants.ENCODER_0, Constants.DIST_PER_TICK);
        enc1 = titan.getEncoder(Constants.ENCODER_2, Constants.DIST_PER_TICK);
        enc2 = titan.getEncoder(Constants.ENCODER_1, Constants.DIST_PER_TICK);
        enc3 = titan.getEncoder(Constants.ENCODER_3, Constants.DIST_PER_TICK);
    }
    
    @Override
    public void initDefaultCommand() {}
    
    public void periodic() {
        SmartDashboard.putNumber("Gyro/Angulo",   gyro.getAngle());

        SmartDashboard.putNumber("Encoder/dist0", enc0.getDistance());
        SmartDashboard.putNumber("Encoder/dist1", enc1.getDistance());
        SmartDashboard.putNumber("Encoder/dist2", enc2.getDistance());
        SmartDashboard.putNumber("Encoder/dist3", enc3.getDistance());
        
        SmartDashboard.putNumber("Servos/Servo0/Angle", servo0.getAngle());
        SmartDashboard.putNumber("Servos/Servo0/Speed", servo0.getSpeed());
        SmartDashboard.putNumber("Servos/Servo0/Pos", servo0.getPosition());
        SmartDashboard.putNumber("Servos/Servo0/Raw", servo0.getPosition());
    
        SmartDashboard.putNumber("Servos/Servo1/Angle", servo1.getAngle());
        SmartDashboard.putNumber("Servos/Servo1/Speed", servo1.getSpeed());
        SmartDashboard.putNumber("Servos/Servo1/Pos", servo1.getPosition());
        SmartDashboard.putNumber("Servos/Servo1/Raw", servo1.getPosition());

        RefreshOdometria(); 
    }
    // =========================================================
    //  Servos
    // =========================================================
    
    /**
     * Controle das funçoes "degree", "speed" e "pos" para movimentação dos servos. essa funçã permite controlar 8 servos.
     * Porém a função chama apenas um servo por vez.
     * <p>
     * @param degrees valid input is 0 to 300
     *
     * Set the PWM value based on a speed. This is intended to be used by speed controllers.
     * <p>
     * @param speed Set the speed controller between -1.0 and 1.0.
     * 
     * Set the PWM value based on a position. This is intended to be used by servos.
     * <p>
     * @param pos The position to set the servo between 0.0 and 1.0.
     * 
     * @param servo Varivel para definir a porta de servo 0-7.
     * 
     * @param func Variavel para ditar a função que o serrvo vai exercer.
     * 
     * @param value É uma variavel Double onde vai receber os valores correspondentes para função desejada.
     * */
    public void ServoController(int servo, String func, Double value){
        
        Servo porta = null ;
        switch(servo){
            case 0: porta = servo0;break; // 
            case 1: porta = servo1;break; // elevador
            case 2: porta = null;break;
            case 3: porta = null;break;
            case 4: porta = null;break;
            case 5: porta = null;break;
            case 6: porta = null;break;
            case 7: porta = null;break;
        }
        
        switch(func){
            case "degree":
                porta.setAngle(value);break;
            case "speed": 
                porta.setSpeed(value);break;
            case "pos": 
                porta.setPosition(value);break;
        }
    }
    /**
     * Set the PWM value directly to the hardware.
     * <p>
     * @param value Raw PWM value. Range 0 - 255.
     */
    public void SetServoRaw(int value){ servo0.setRaw(value); }

    public void stopServo(){
        servo0.setDisabled(); // extensao
        servo1.setDisabled(); // elevador
    }
    // =========================================================
    //  BLOCO — moverDistancia
    // =========================================================
    public void moverDistancia(String dir, double metros) {
        if (!moveIniciado) {
            enc0.reset(); enc1.reset();
            enc2.reset(); enc3.reset();
            moveIniciado = true;
            return;
        }

        double d0 = Math.abs(enc0.getDistance());
        double d1 = Math.abs(enc1.getDistance());
        double d2 = Math.abs(enc2.getDistance());
        double d3 = Math.abs(enc3.getDistance());
        double distPercorrida = (d0 + d1) / 2.0;

        SmartDashboard.putNumber("Move/dist_mm", distPercorrida);

        if (distPercorrida >= metros) {
            stopMotors();
            moveConcluido = true;
            moveIniciado = false;
            return;
        }

        double v = Constants.VEL_MOVE;
        switch (dir) {
            case "F":  setMotors( v,  -v, v, -v); break;
            case "B":  setMotors(-v,  v,  -v,  v); break;

            case "SR": setMotors( v-0.2,  v-0.2,  v, -v); break;
            
            case "SL": setMotors(-v+0.2, -v+0.2, -v+0.2, -v+0.2); break;
            default:   stopMotors();
        }
    }
    // =========================================================
    //  Odometria
    // =========================================================
    private double ponto_inicial_X = 0.0;
    private double ponto_inicial_Y = 0.0;

    public void setPontoInicial(double x, double y) {
        ponto_inicial_X = x;
        ponto_inicial_Y = y;
        odom_X = x;
        odom_Y = y;
    }

    public void resetOdometria() {
        odom_X        = ponto_inicial_X;
        odom_Y        = ponto_inicial_Y;
        enc0_anterior = 0.0;
        enc1_anterior = 0.0;
        gyro.zeroYaw();
        enc0.reset();
        enc1.reset();
    }

    public enum Orientacao_robot {
        FRENTE,    //   0° ± 22.5°
        DIREITA,   //  90° ± 22.5°
        TRAS,      // 180° ± 22.5°
        ESQUERDA,  // -90° ± 22.5°
        NORDESTE,  // entre FRENTE e DIREITA   (  0° ~  90°)
        SUDESTE,   // entre DIREITA e TRAS     ( 90° ~ 180°)
        SUDOESTE,  // entre TRAS e ESQUERDA    (-90° ~-180°)
        NOROESTE   // entre ESQUERDA e FRENTE  (-90° ~   0°)
    
    }
    public Orientacao_robot orienta = Orientacao_robot.FRENTE;

    /**
     * Atualiza a posição do robô no plano cartesiano da arena.
     * 
     * FRENTE   → Y + (a partir de ponto_inicial_Y, até Arena_max_Y_mm)
     * TRAS     → Y - (a partir de ponto_inicial_Y, até 0)
     * DIREITA  → X + (a partir de ponto_inicial_X, até Arena_max_X_mm)
     * ESQUERDA → X - (a partir de ponto_inicial_X, até 0)
     */
    public void RefreshOdometria() {
        double Arena_max_X_mm = 1800.0;
        double Arena_max_Y_mm = 2700.0;

        // ── delta deste ciclo ─────────────────────────────────
        double enc0_atual = enc0.getDistance();
        double enc1_atual = enc1.getDistance();

        double delta0 = Math.abs(enc0_atual - enc0_anterior);
        double delta1 = Math.abs(enc1_atual - enc1_anterior);
        double delta  = (delta0 + delta1) / 2.0;

        enc0_anterior = enc0_atual;
        enc1_anterior = enc1_atual;

        // ── orientação atual ──────────────────────────────────
        double ang = normalizeAngle180(gyro.getAngle());

        if      (ang >= -22.5  && ang <  22.5)  orienta = Orientacao_robot.FRENTE;
        else if (ang >=  67.5  && ang < 112.5)  orienta = Orientacao_robot.DIREITA;
        else if (ang >= 157.5  || ang < -157.5) orienta = Orientacao_robot.TRAS;
        else if (ang >= -112.5 && ang < -67.5)  orienta = Orientacao_robot.ESQUERDA;

        // ── diagonais ─────────────────────────────────────────────
        else if (ang >=  22.5  && ang <  67.5)  orienta = Orientacao_robot.NORDESTE;
        else if (ang >= 112.5  && ang < 157.5)  orienta = Orientacao_robot.SUDESTE;
        else if (ang >= -157.5 && ang < -112.5) orienta = Orientacao_robot.SUDOESTE;
        else                                    orienta = Orientacao_robot.NOROESTE;

        // ── incrementa a partir do ponto inicial ──────────────
        switch (orienta) {
            case FRENTE:   odom_Y += delta; break;
            case TRAS:     odom_Y -= delta; break;
            case DIREITA:  odom_X += delta; break;
            case ESQUERDA: odom_X -= delta; break;
        }

        // ── limita dentro da arena ────────────────────────────
        odom_X = clamp(odom_X, 0.0, Arena_max_X_mm);
        odom_Y = clamp(odom_Y, 0.0, Arena_max_Y_mm);

        // ── dashboard ─────────────────────────────────────────
        SmartDashboard.putNumber("Odom/X_mm",        odom_X);
        SmartDashboard.putNumber("Odom/Y_mm",        odom_Y);
        SmartDashboard.putNumber("Odom/Ponto_ini_X", ponto_inicial_X);
        SmartDashboard.putNumber("Odom/Ponto_ini_Y", ponto_inicial_Y);
        SmartDashboard.putNumber("Odom/Angulo",      gyro.getAngle());
        SmartDashboard.putString("Odom/Orientacao",  orienta.toString());
        SmartDashboard.putBoolean("Odom/Limite_X",   odom_X <= 0.0 || odom_X >= Arena_max_X_mm);
        SmartDashboard.putBoolean("Odom/Limite_Y",   odom_Y <= 0.0 || odom_Y >= Arena_max_Y_mm);
    }

    public double getX()                { return odom_X; }
    public double getY()                { return odom_Y; }
    public Orientacao_robot getOrient() { return orienta; }

    public void ir_posicao_X(double alvo_X) {
        if (!moveIniciado) {
            moveIniciado           = true;
            moveConcluido          = false;
            giroOrientacaoIniciado = false;
            giroConcluido          = false;
            return;
        }
    
        Orientacao_robot orientacao_necessaria =
            (alvo_X > odom_X) ? Orientacao_robot.DIREITA : Orientacao_robot.ESQUERDA;
    
        boolean orientadoCorretamente;
        if (orientacao_necessaria == Orientacao_robot.DIREITA) {
            orientadoCorretamente = (orienta == Orientacao_robot.DIREITA && gyro.getAngle() >= 90.0);
        } else {
            orientadoCorretamente = (orienta == Orientacao_robot.ESQUERDA && gyro.getAngle() <= -90.0);
        }
    
        SmartDashboard.putBoolean("Pos/Orientado",    orientadoCorretamente);
        SmartDashboard.putNumber("Pos/Angulo_atual",  gyro.getAngle());
    
        if (!orientadoCorretamente) {
            double grausAlvo = (orientacao_necessaria == Orientacao_robot.DIREITA) ? 90.0 : -90.0;
            girarParaAngulo(grausAlvo);
            SmartDashboard.putString("Pos/Estado", "Girando para " + orientacao_necessaria);
            return;
        }
    
        giroOrientacaoIniciado = false;
        giroConcluido          = false;
    
        double erro = Math.abs(odom_X - alvo_X);
        SmartDashboard.putNumber("Pos/erro_X", erro);
        SmartDashboard.putString("Pos/Estado", "Movendo X");
    
        if (erro <= 20.0) {
            stopMotors();
            moveConcluido = true;
            moveIniciado  = false;
            return;
        }
    
        // ── correção de ângulo ± 1° pelas rodas traseiras ────
        double anguloAlvo   = (orientacao_necessaria == Orientacao_robot.DIREITA) ? 90.0 : -90.0;
        double erroAngulo   = normalizeAngle180(anguloAlvo - gyro.getAngle());
        double correcao = clamp(erroAngulo * 0.08, -0.3, 0.3);
    
        SmartDashboard.putNumber("Pos/erroAngulo", erroAngulo);
        SmartDashboard.putNumber("Pos/correcao",   correcao);
    
        double v = 0.5;
        // motor2 e motor3 são as rodas traseiras
        // correcao positiva → erro pra direita → trase compensa
        setMotors(v, -v, v + correcao, -(v + correcao));
    }
    
    public void ir_posicao_Y(double alvo_Y) {
        if (!moveIniciado) {
            moveIniciado           = true;
            moveConcluido          = false;
            giroOrientacaoIniciado = false;
            giroConcluido          = false;
            return;
        }
    
        Orientacao_robot orientacao_necessaria =
            (alvo_Y > odom_Y) ? Orientacao_robot.FRENTE : Orientacao_robot.TRAS;
    
        boolean orientadoCorretamente;
        if (orientacao_necessaria == Orientacao_robot.FRENTE) {
            orientadoCorretamente = (orienta == Orientacao_robot.FRENTE &&
                                     gyro.getAngle() >= -1.0 && gyro.getAngle() <= 1.0);
        } else {
            orientadoCorretamente = (orienta == Orientacao_robot.TRAS &&
                                     (gyro.getAngle() >= 179.0 || gyro.getAngle() <= -179.0));
        }
    
        SmartDashboard.putBoolean("Pos/Orientado",   orientadoCorretamente);
        SmartDashboard.putNumber("Pos/Angulo_atual", gyro.getAngle());
    
        if (!orientadoCorretamente) {
            double grausAlvo = (orientacao_necessaria == Orientacao_robot.FRENTE) ? 0.0 : 180.0;
            girarParaAngulo(grausAlvo);
            SmartDashboard.putString("Pos/Estado", "Girando para " + orientacao_necessaria);
            return;
        }
    
        giroOrientacaoIniciado = false;
        giroConcluido          = false;
    
        double erro = Math.abs(odom_Y - alvo_Y);
        SmartDashboard.putNumber("Pos/erro_Y", erro);
        SmartDashboard.putString("Pos/Estado", "Movendo Y");
    
        if (erro <= 20.0) {
            stopMotors();
            moveConcluido = true;
            moveIniciado  = false;
            return;
        }
    
        // ── correção de ângulo ± 1° pelas rodas traseiras ────
        double anguloAlvo = (orientacao_necessaria == Orientacao_robot.FRENTE) ? 0.0 : 180.0;
        double erroAngulo = normalizeAngle180(anguloAlvo - gyro.getAngle());
        double correcao   = clamp(erroAngulo * 0.02, -0.15, 0.15);
    
        SmartDashboard.putNumber("Pos/erroAngulo", erroAngulo);
        SmartDashboard.putNumber("Pos/correcao",   correcao);
    
        double v = 0.5;
        setMotors(v, -v, v + correcao, -(v + correcao));
    }
    // =========================================================
    //  BLOCO — executarGiro
    // =========================================================
    private boolean giroOrientacaoIniciado = false;

    public void girarParaAngulo(double grausAlvo) {
        // NÃO reseta o gyro — usa ângulo absoluto
        if (!giroOrientacaoIniciado) {
            ciclosEstaveis        = 0;
            giroOrientacaoIniciado = true;
            return;
        }

        double erro = normalizeAngle180(grausAlvo - gyro.getAngle());

        SmartDashboard.putNumber("Giro/Orientacao/erro",          erro);
        SmartDashboard.putNumber("Giro/Orientacao/ciclosEstaveis", ciclosEstaveis);
        SmartDashboard.putNumber("Giro/Orientacao/alvo",          grausAlvo);
        SmartDashboard.putNumber("Giro/Orientacao/atual",         gyro.getAngle());

        if (Math.abs(erro) < Constants.TOLERANCE_DEG) {
            ciclosEstaveis++;
            stopMotors();
            if (ciclosEstaveis >= Constants.CICLOS_ESTAVEIS) {
                giroConcluido          = true;
                giroOrientacaoIniciado = false;
                ciclosEstaveis         = 0;
            }
            return;
        }

        ciclosEstaveis = 0;
        double omega = clamp(
            applyDeadband(Constants.KP_ROT * erro, Constants.DEADBAND_ROT),
            -Constants.VEL_GIRO, Constants.VEL_GIRO
        );
        setMotors(omega, omega, omega, omega);
    }

    // =========================================================
    //  Controle direto
    // =========================================================
    public void setMotors(double m0, double m1, double m2, double m3) {
        motor0.set(m0); motor1.set(m1);
        motor2.set(m2); motor3.set(m3);
    }

    public void stopMotors() { setMotors(0, 0, 0, 0); }

    public void resetBlocos() {
        moveIniciado           = false;
        moveConcluido          = false;
        giroIniciado           = false;
        giroConcluido          = false;
        giroOrientacaoIniciado = false; 
        ciclosEstaveis         = 0;
    }

    public void resetGyro()     { gyro.zeroYaw(); }
    public void resetEncoders() {
        enc0.reset(); enc1.reset();
        enc2.reset(); enc3.reset();
    }
    public double getAngulo() { return gyro.getAngle(); }

    // ── Utilitários ───────────────────────────────────────────
    private double applyDeadband(double v, double db) {
        if (Math.abs(v) < db) return 0.0;
        return Math.signum(v) * clamp((Math.abs(v) - db) / (1.0 - db), 0.0, 1.0);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private double normalizeAngle180(double deg) {
        while (deg >  180.0) deg -= 360.0;
        while (deg < -180.0) deg += 360.0;
        return deg;
    }

} 