package frc.robot;

public final class Constants {

    // Titan
    public static final int    TITAN_ID      = 42;
    public static final int    MOTOR_0       = 0;
    public static final int    MOTOR_1       = 1;
    public static final int    MOTOR_2       = 2;
    public static final int    MOTOR_3       = 3;
    public static final int    ENCODER_0     = 0;
    public static final int    ENCODER_1     = 1;
    public static final int    ENCODER_2     = 2;
    public static final int    ENCODER_3     = 3;
    public static final double DIST_PER_TICK = 0.210843; // mm/tick — calibrado pela trena
        
    // Servos
    public static final int SERVO_0            = 8;
    public static final int SERVO_1            = 9;
    
    // Drive
    public static final double VEL_MOVE        = 0.4;
    public static final double VEL_GIRO        = 0.3;
    public static final double KP_ROT          = 0.1;
    public static final double DEADBAND_ROT    = 0.15;
    public static final double TOLERANCE_DEG   = 3.0;
    public static final int    CICLOS_ESTAVEIS = 10;
    

    // Botões / LEDs
    public static final int BTN_START = 10; // amarelo sinal
    public static final int BTN_STOP  = 8; // marrom sinal

    public static final int LEDRun    = 17;
    public static final int LEDStop   = 16;

    // Sensores
    public static final int TRIG1  = 12;
    public static final int ECHO1  = 11; // direita

    public static final int TRIG2  = 14;
    public static final int ECHO2  = 9; // esquerda

    public static final int ULTRA1 = 0; // IR esquerda
    public static final int ULTRA2 = 1; // IR direita
}