// package frc.robot;

// import com.studica.frc.Titan;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// /**
//  * Odometria e cinemática para chassi omni 3 rodas — kit WorldSkills Studica.
//  *
//  * Layout (vista de cima):
//  *
//  *         M0 (frente — roda perpendicular, rola para X)
//  *          ▲
//  *         / \
//  *        /   \
//  *      M2     M3
//  *   (esq)     (dir)
//  *
//  * Ângulos das rodas no frame do robô (sentido anti-horário desde X+):
//  *   M0 = 90°  → sin=1,  cos=0
//  *   M2 = 210° → sin=-0.5, cos=-√3/2
//  *   M3 = 330° → sin=-0.5, cos=+√3/2
//  *
//  * Cinemática inversa padrão omni 3 rodas (robot-centric, ω CCW+):
//  *   vi = -sin(αi)*vx + cos(αi)*vy + R*ω
//  *
//  *   M0: v0 = -sin(90°)*vx  + cos(90°)*vy  + R*ω = -vx        + R*ω
//  *   M2: v2 = -sin(210°)*vx + cos(210°)*vy + R*ω = +0.5*vx - (√3/2)*vy + R*ω
//  *   M3: v3 = -sin(330°)*vx + cos(330°)*vy + R*ω = +0.5*vx + (√3/2)*vy + R*ω
//  *
//  * Sinais dos encoders medidos fisicamente (sem negar nenhum):
//  *   Y+ (frente): dist0≈0, dist2<0, dist3<0
//  *   X+ (direita): dist0>0, dist2≈0, dist3<0
//  *
//  * Como os sinais físicos são opostos à cinemática geométrica acima,
//  * aplicamos fator de correção de sinal K em cada encoder:
//  *   K0 = -1  (geométrico diz -vx → encoder lê positivo em X+, OK com -1)
//  *   K2 = -1  (geométrico diz +0.5vx-(√3/2)vy → encoder lê negativo em Y+)
//  *   K3 = +1  (geométrico diz +0.5vx+(√3/2)vy → encoder lê negativo, então K=+1 inverte)
//  *
//  * Simplificando: multiplicamos as leituras pelos fatores K antes de usar.
//  */
// public class ThreeWheelOmniOdometry {

//     // Constantes geométricas
//     private static final double ROBOT_RADIUS_M = 0.175;
//     private static final double SIN30 = 0.5;
//     private static final double COS30 = Math.sqrt(3.0) / 2.0; // ≈ 0.866

//     // Fatores de correção de sinal — derivados dos testes físicos reais.
//     // Se o robô se comportar errado em algum eixo, troque o sinal do K correspondente.
//     private static final double K0 =  -1.0; // M0: encoder positivo = X- físico
//     private static final double K2 =  -1.0; // M2: encoder negativo = Y+ físico
//     private static final double K3 =  -1.0; // M3: encoder negativo = Y+ e X+ físico

//     private final Titan.Encoder encoder_0;
//     private final Titan.Encoder encoder_2;
//     private final Titan.Encoder encoder_3;

//     private double x     = 0.0;
//     private double y     = 0.0;
//     private double theta = 0.0;

//     private double lastDist0 = 0.0;
//     private double lastDist2 = 0.0;
//     private double lastDist3 = 0.0;

//     public ThreeWheelOmniOdometry(Titan.Encoder enc0,
//                                    Titan.Encoder enc2,
//                                    Titan.Encoder enc3) {
//         this.encoder_0 = enc0;
//         this.encoder_2 = enc2;
//         this.encoder_3 = enc3;
//     }

//     public void updateWithGyro(double gyroAngleDeg) {
//         double newTheta = Math.toRadians(gyroAngleDeg);

//         // Leitura bruta corrigida pelo fator físico de cada encoder
//         double dist0 = K0 * encoder_0.getDistance();
//         double dist2 = K2 * encoder_2.getDistance();
//         double dist3 = K3 * encoder_3.getDistance();

//         double dW0 = dist0 - lastDist0;
//         double dW2 = dist2 - lastDist2;
//         double dW3 = dist3 - lastDist3;

//         lastDist0 = dist0;
//         lastDist2 = dist2;
//         lastDist3 = dist3;

//         // Cinemática direta — inversa da matriz de cinemática inversa.
//         // Para omni 3 rodas 120°:
//         //   vx = (-2/3)*dW0 + (1/3)*dW2 + (1/3)*dW3
//         //   vy =              (1/√3)*dW2 - (1/√3)*dW3
//         // (o termo de rotação cancela-se na inversão)
//         double dXlocal = (-2.0/3.0)*dW0 + (1.0/3.0)*dW2 + (1.0/3.0)*dW3;
//         double dYlocal = (1.0 / (Math.sqrt(3.0))) * (dW2 - dW3);

//         // Rotação campo → local (integração trapezoidal)
//         double midTheta = (theta + newTheta) / 2.0;
//         double dXglobal = Math.cos(midTheta) * dXlocal - Math.sin(midTheta) * dYlocal;
//         double dYglobal = Math.sin(midTheta) * dXlocal + Math.cos(midTheta) * dYlocal;

//         x     += dXglobal;
//         y     += dYglobal;
//         theta  = newTheta;

//         publishTelemetry();
//     }

//     /**
//      * Cinemática inversa — robot-centric.
//      * vi = -sin(αi)*vx + cos(αi)*vy + R*ω
//      * Multiplicado pelo fator Ki para alinhar com o sinal físico do motor.
//      *
//      * Como Ki = -1 para todos, o sinal de cada speed é invertido,
//      * o que faz os motores girarem na direção física correta.
//      */
//     public double[] robotCentricSpeeds(double vx, double vy, double omega) {
//         double[] speeds = new double[3];

//         // Geométrico puro (antes da correção de sinal):
//         // raw0 = -vx           + R*ω
//         // raw2 = +SIN30*vx - COS30*vy + R*ω
//         // raw3 = +SIN30*vx + COS30*vy + R*ω
//         //
//         // Multiplicado por Ki (todos -1) para alinhar com físico:
//         speeds[0] = K0 * (-vx              + ROBOT_RADIUS_M * omega); // M0
//         speeds[1] = K2 * ( SIN30*vx - COS30*vy + ROBOT_RADIUS_M * omega); // M2
//         speeds[2] = K3 * ( SIN30*vx + COS30*vy + ROBOT_RADIUS_M * omega); // M3

//         return normalize(speeds);
//     }

//     public double[] fieldCentricSpeeds(double vxField, double vyField, double omega) {
//         // Rotação campo → robô
//         double vxLocal =  Math.cos(theta) * vxField + Math.sin(theta) * vyField;
//         double vyLocal = -Math.sin(theta) * vxField + Math.cos(theta) * vyField;
//         return robotCentricSpeeds(vxLocal, vyLocal, omega);
//     }

//     public double getX()        { return x; }
//     public double getY()        { return y; }
//     public double getThetaRad() { return theta; }
//     public double getThetaDeg() { return Math.toDegrees(theta); }

//     public void resetPose(double startX, double startY, double startDeg) {
//         x     = startX;
//         y     = startY;
//         theta = Math.toRadians(startDeg);

//         encoder_0.reset();
//         encoder_2.reset();
//         encoder_3.reset();

//         lastDist0 = lastDist2 = lastDist3 = 0.0;
//     }

//     private double[] normalize(double[] speeds) {
//         double max = 0;
//         for (double s : speeds) max = Math.max(max, Math.abs(s));
//         if (max > 1.0)
//             for (int i = 0; i < speeds.length; i++) speeds[i] /= max;
//         return speeds;
//     }

//     private void publishTelemetry() {
//         // SmartDashboard.putNumber("Odom/X_m",        x);
//         // SmartDashboard.putNumber("Odom/Y_m",        y);
//         // SmartDashboard.putNumber("Odom/Theta_deg",  getThetaDeg());
//         // SmartDashboard.putNumber("Encoder/dist0_m", lastDist0);
//         // SmartDashboard.putNumber("Encoder/dist2_m", lastDist2);
//         // SmartDashboard.putNumber("Encoder/dist3_m", lastDist3);
//     }
// }