// package frc.robot;

// import edu.wpi.first.wpilibj.command.CommandGroup;
// import frc.robot.commands.ExecutarGiro;
// import frc.robot.commands.MoverDistancia;

// public class ExplorarLidar extends CommandGroup {

//     public ExplorarLidar() {
//         // ── Rota fixa ──────────────────────────────────────────
//         addSequential(new MoverDistancia("F", 0.55));
//         addSequential(new ExecutarGiro(90));
//         addSequential(new MoverDistancia("F", 0.9));
//         addSequential(new ExecutarGiro(-90));
//         addSequential(new MoverDistancia("F", 0.45));
//         addSequential(new ExecutarGiro(-85));
//         addSequential(new MoverDistancia("F", 0.8));
//         addSequential(new ExecutarGiro(90));
//         addSequential(new MoverDistancia("F", 1.0));

//         // ── Exploração por LiDAR ───────────────────────────────
//         addSequential(new frc.robot.commands.ExplorarLidar());
//     }
// }