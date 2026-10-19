// package frc.robot.subsystems;

// import edu.wpi.cscore.UsbCamera;
// import edu.wpi.first.cameraserver.CameraServer;
// import edu.wpi.first.wpilibj.command.Subsystem;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import frc.robot.Constants;

// public class VisionController extends Subsystem{
   
//     public VisionController() { 
//         new Thread(() -> {
//             UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
//             camera.setResolution(640, 480);
//         }).start();
//     }
    
//     public void periodic() {
//         SmartDashboard.putNumber("Camera/Detect",  0 );
//     }
    
//     @Override
//     public void initDefaultCommand() {}
// }