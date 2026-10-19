package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.Timer;

public class SensorSubsystem extends Subsystem {

    // private final Lidar      lidar;
    // private Lidar.ScanData   scanData;
    private AnalogInput       ultraDireita;
    private AnalogInput       ultraEsquerda;
    private Ultrasonic        ultraFrente;
    private Ultrasonic        ultraTras;
    // private final AnalogInput irEsquerda, irDireita;

    private static final float DIST_MIN = 120.0f;
    private static final float DIST_MAX = 8000.0f;

    private boolean paredevista = false;
    private boolean semparede   = false;

    public SensorSubsystem() {
        try { // ciclo para ultrasonic funcionar
            ultraFrente = new Ultrasonic(Constants.TRIG1, Constants.ECHO1);
            ultraTras = new Ultrasonic(Constants.TRIG2, Constants.ECHO2);
            ultraFrente.setAutomaticMode(true);
            ultraTras.setAutomaticMode(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ultraEsquerda = new AnalogInput(Constants.ULTRA1);
        ultraDireita  = new AnalogInput(Constants.ULTRA2);
    }

    @Override
    public void initDefaultCommand() {}

    public void periodic() {
        SmartDashboard.putNumber("Sensor/Frente_mm",   getUltraFrente());
        SmartDashboard.putNumber("Sensor/Esquerda_cm", getIrEsquerda());
        SmartDashboard.putNumber("Sensor/Direita_cm",  getIrDireita());

        See_wall();
    }

    public enum Parededetect {
        PAREDE_FRENTE,
        PAREDE_NORDESTE,
        PAREDE_DIREITA,
        PAREDE_SUDESTE,
        PAREDE_TRAS,
        PAREDE_SUDOESTE,
        PAREDE_ESQUERDA,
        PAREDE_NOROESTE,
        SEM_PAREDE,
        PAREDE_2_LATERAIS,
        PAREDE_r_FRENTE,
        PAREDE_l_FRENTE,
        PAREDE_TODOS_LADOS
    }
    public Parededetect parede = Parededetect.SEM_PAREDE;

    public double distância_parede_lateral = 15.0;  //cm
    public double distância_parede_frente  = 150.0; //mm

    double largura_robo             = 400.0;
    double largura_passagem         = 600.0;

    public void See_wall() {
        double Frente_distancia   = getUltraFrente();
        double Esquerda_distancia = getIrEsquerda();
        double Direita_distancia  = getIrDireita();
        double Tras_distancia     = getUltraTras();
    
        boolean temF = temParedeFrente();
        boolean temE = temParedeEsquerda();
        boolean temD = temParedeDireita();
    
        if      (temF && temE && temD) parede = Parededetect.PAREDE_TODOS_LADOS;
        else if (temF && temD)         parede = Parededetect.PAREDE_r_FRENTE;
        else if (temF && temE)         parede = Parededetect.PAREDE_l_FRENTE;
        else if (temE && temD)         parede = Parededetect.PAREDE_2_LATERAIS;
        else if (temF)                 parede = Parededetect.PAREDE_FRENTE;
        else if (temD)                 parede = Parededetect.PAREDE_DIREITA;
        else if (temE)                 parede = Parededetect.PAREDE_ESQUERDA;
        else                           parede = Parededetect.SEM_PAREDE;
    
        SmartDashboard.putNumber("Parede/Distancia_direita",  Direita_distancia);
        SmartDashboard.putNumber("Parede/Distancia_esquerda", Esquerda_distancia);
        SmartDashboard.putNumber("Parede/Distancia_frente",   Frente_distancia);
        SmartDashboard.putNumber("Parede/Distancia_tras",     Tras_distancia);
        SmartDashboard.putString("Parede/detect",             parede.toString());
    }
    public Parededetect getParede() {return parede;}

    //── Ultrassônico ──────────────────────────────────────────
    public double getUltraFrente() {
        // ultraFrente.ping();
        // Timer.delay(0.005);
        return ultraFrente.getRangeMM();
    }
    public double getUltraTras() {
        // ultraFrente.ping();
        // Timer.delay(0.005);
        return ultraTras.getRangeMM();
    }
    //── IR ────────────────────────────────────────────────────
    public double getIrEsquerda() {
        double v = ultraEsquerda.getAverageVoltage();
        if (v <= 0.1) return -1.0;
        return Math.pow(v, -1.2045) * 27.726;
    }

    public double getIrDireita() {
        double v = ultraDireita.getAverageVoltage();
        if (v <= 0.1) return -1.0;
        return Math.pow(v, -1.2045) * 27.726;
    }
    //_____________________________________________________________
    public boolean temParedeFrente() {

        double distancia = getUltraFrente();
    
        return distancia > 0.0 && distancia < distância_parede_frente;
    }
    // public boolean temParedeTras() {

    //     double distancia = getUltraTras();
    
    //     return distancia > 0.0 && distancia < 200.0;
    // }

    public boolean temParedeEsquerda() {

        double distancia = getIrEsquerda();
    
        return distancia > 0.0 && distancia < distância_parede_lateral;
    }
    public boolean temParedeDireita() {

        double distancia = getIrDireita();
    
        return distancia > 0.0 && distancia < distância_parede_lateral;
    }
}