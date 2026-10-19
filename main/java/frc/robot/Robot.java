package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.gamepad.OI;
import frc.robot.gamepad.OI.EstadoRobo;
import frc.robot.subsystems.SensorSubsystem;
import frc.robot.subsystems.SensorSubsystem.Parededetect;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

public class Robot extends TimedRobot {
    // config shuffleboard
    private final PowerDistributionPanel pdp = new PowerDistributionPanel();

    private EstadoRobo estado = EstadoRobo.PARADO; 

    @Override
    public void robotInit() {
        ShuffleboardTab tab = Shuffleboard.getTab("Robot_V1.0");
        RobotContainer.oi.indicarParado(); 
        SmartDashboard.putData("PDP", pdp);
        SmartDashboard.putData("Drive Subsystem", RobotContainer.drive);
        SmartDashboard.putData("Sensor Subsystem", RobotContainer.sensors);
        SmartDashboard.putBoolean("Button Stop", false);
        SmartDashboard.putBoolean("Button Start", false);
        RobotContainer.drive.resetEncoders();
    } 

    @Override
    public void robotPeriodic() {
        Scheduler.getInstance().run();

        RobotContainer.drive.periodic();
        RobotContainer.sensors.periodic(); 
        
        if (RobotContainer.oi.getPanelStartPressed()) {
            RobotContainer.ds.enable();
            RobotContainer.oi.indicarRodando();
            estado = EstadoRobo.MOVENDO;
            RobotContainer.drive.resetBlocos();
            SmartDashboard.putBoolean("Button Stop", false);
            SmartDashboard.putBoolean("Button Start", true);
        }
        if (RobotContainer.oi.getPanelStopPressed()) {
            RobotContainer.ds.disable();
            estado = EstadoRobo.PARADO;
            RobotContainer.oi.indicarParado();
            RobotContainer.navigation.parar();
            RobotContainer.drive.stopMotors();
            SmartDashboard.putBoolean("Button Stop",true);
            SmartDashboard.putBoolean("Button Start",false);
        }
        SmartDashboard.putString("Estado_Robo:", estado.toString());
    }

    @Override
    public void teleopInit() {}

    @Override
    public void teleopPeriodic() {} 

    @Override
    public void autonomousInit() {
        /**
         * Iniciar os servos na posição que for do mecanismo
         */
        // new ExplorarLidar().start();
        RobotContainer.drive.setPontoInicial(300.0, 300.0); // posição inicial na arena em mm
        RobotContainer.drive.resetOdometria();

        RobotContainer.drive.ServoController(0, "degree", 150.0);

        RobotContainer.drive.ServoController(1, "degree", -150.0); // elevador

        RobotContainer.navigation.iniciar();
    }

    @Override
    public void autonomousPeriodic() {
        /**
         * caminh0= home,[1.675;0.3],[1.675;2.932],[0.325;2.164],[0.325;3.65],[1.0;2.164],[1.675;1.293],home
         * só é possivel ir pra uma [X ou Y] cordenada por vez!
         */
        RobotContainer.navigation.periodic();
    }
    @Override
    public void disabledInit() {
        Scheduler.getInstance().removeAll();
        RobotContainer.drive.stopMotors();
        RobotContainer.oi.indicarParado();
    }
    @Override
    public void disabledPeriodic() {
        RobotContainer.drive.stopMotors();
        RobotContainer.drive.stopServo();
    }
}