// package frc.robot.commands;

// import edu.wpi.first.wpilibj.command.Command;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import frc.robot.RobotContainer;
// import frc.robot.subsystems.DriveSubsystem;
// import frc.robot.subsystems.SensorSubsystem;

// public class ExplorarLidar extends Command {

//     private final DriveSubsystem  drive;
//     private final SensorSubsystem sensors;

//     private static final float  LIMIAR_FRENTE  = 400.0f;
//     private static final float  LIMIAR_LATERAL = 350.0f;
//     private static final double PASSO_AVANCO   = 0.3;

//     private enum Estado { DECIDIR, MOVER_FRENTE, GIRAR_DIREITA, GIRAR_ESQUERDA, PARADO }
//     private Estado estado;

//     public ExplorarLidar() {
//         this.drive   = RobotContainer.drive;
//         this.sensors = RobotContainer.sensors;
//         requires(drive);
//     }

//     @Override
//     protected void initialize() {
//         drive.resetBlocos();
//         estado = Estado.DECIDIR;
//     }

//     @Override
//     protected void execute() {
//         SmartDashboard.putString("Lidar/Estado", estado.toString());

//         switch (estado) {
//             case DECIDIR:
//                 drive.stopMotors();
//                 drive.resetBlocos();
//                 if      (!sensors.paredeFrente(LIMIAR_FRENTE))   estado = Estado.MOVER_FRENTE;
//                 else if (!sensors.paredeDireita(LIMIAR_LATERAL))  estado = Estado.GIRAR_DIREITA;
//                 else if (!sensors.paredeEsquerda(LIMIAR_LATERAL)) estado = Estado.GIRAR_ESQUERDA;
//                 else                                              estado = Estado.PARADO;
//                 break;

//             case MOVER_FRENTE:
//                 drive.moverDistancia("F", PASSO_AVANCO);
//                 if (drive.moveConcluido) estado = Estado.DECIDIR;
//                 break;

//             case GIRAR_DIREITA:
//                 drive.executarGiro(90);
//                 if (drive.giroConcluido) estado = Estado.DECIDIR;
//                 break;

//             case GIRAR_ESQUERDA:
//                 drive.executarGiro(-90);
//                 if (drive.giroConcluido) estado = Estado.DECIDIR;
//                 break;

//             case PARADO:
//             default:
//                 drive.stopMotors();
//                 break;
//         }
//     }

//     @Override
//     protected boolean isFinished() { return false; }

//     @Override
//     protected void end() { drive.stopMotors(); drive.resetBlocos(); }

//     @Override
//     protected void interrupted() { end(); }
// }