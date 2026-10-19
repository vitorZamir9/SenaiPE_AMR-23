// package frc.robot.subsystems;

// import com.studica.frc.Lidar;

// import edu.wpi.first.wpilibj.command.Subsystem;

// public class LidarController extends Subsystem{

//     public LidarController(){
//         // lidar = new Lidar(Lidar.Port.kUSB1);
//         // lidar.clusterConfig(50.0f, 5);
//         // lidar.enableFilter(Lidar.Filter.kCLUSTER, true);
//     }
//     public void periodic() {
//         // SmartDashboard.putNumber("Lidar/Frente_mm",    getLidarFrente());
//         // SmartDashboard.putNumber("Lidar/Direita_mm",   getLidarDireita());
//         // SmartDashboard.putNumber("Lidar/Esquerda_mm",  getLidarEsquerda());
//     }
//     // ── LiDAR ─────────────────────────────────────────────────
//     // public float getMenorDistancia(int inicio, int fim) {
//     //     scanData = lidar.getData();
//     //     if (scanData == null) return DIST_MAX;
//     //     float menor = DIST_MAX;
//     //     for (int g = inicio; g <= fim; g++) {
//     //         int   idx = ((g % 360) + 360) % 360;
//     //         float d   = scanData.distance[idx];
//     //         if (d >= DIST_MIN && d <= DIST_MAX && d < menor) menor = d;
//     //     }
//     //     return menor;
//     // }

//     // public float getLidarFrente()   { return getMenorDistancia(355, 360); }
//     // public float getLidarDireita()  { return getMenorDistancia(85,  95);  }
//     // public float getLidarEsquerda() { return getMenorDistancia(265, 275); }
//     // public float getLidarTras()     { return getMenorDistancia(175, 185); }

//     // public boolean paredeFrente(float limiar)   { return getLidarFrente()   < limiar; }
//     // public boolean paredeDireita(float limiar)  { return getLidarDireita()  < limiar; }
//     // public boolean paredeEsquerda(float limiar) { return getLidarEsquerda() < limiar; }
    
//     @Override
//     public void initDefaultCommand() {}
// }