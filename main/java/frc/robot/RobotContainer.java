package frc.robot;

import com.studica.frc.MockDS;

import frc.robot.gamepad.OI;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.networktables.NetworkTableEntry;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.SensorSubsystem;
import frc.robot.subsystems.NavigationSubsystem;;
// import frc.robot.subsystems.VisionController;
// import frc.robot.subsystems.LidarController;

public class RobotContainer{
    // ── Subsystems ────────────────────────────────────────────
    public static final DriveSubsystem  drive   = new DriveSubsystem();
    public static final SensorSubsystem sensors = new SensorSubsystem();
    public static final NavigationSubsystem navigation = new NavigationSubsystem();
    // public static final VisionController cam    = new VisionController();
    // public static final LidarController LidaR   = new LidarController();
    // ── Inputs ────────────────────────────────────────────────
    public static final OI     oi = new OI();
    public static final MockDS ds = new MockDS();
    
    private double ultra_direita    = 0.0; 
    private double ultra_esquerda   = 0.0;
    private double ultra_frente     = 0.0;

    /**──────────────────────────────────────────────────────────────────────────**/
    private double distancia_parede = 170.0;
    
    private double largura_roboMM       = 410.0;
    private double comprimento_roboMM   = 430.0;
    public RobotContainer(){}
}