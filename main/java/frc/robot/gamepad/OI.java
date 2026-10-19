package frc.robot.gamepad;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class OI
{
    // ── PS4 ───────────────────────────────────────────────────
    // private final Joystick drivePad;

    // ── Control Panel ─────────────────────────────────────────
    private final DigitalInput  panelBtnStart;
    private final DigitalInput  panelBtnStop;

    private final DigitalOutput panelLedRun;
    private final DigitalOutput panelLedStop;

    // Estado anterior dos botões (detectar borda de descida)
    private boolean lastStart = false;
    private boolean lastStop  = false;

    public OI(){
        panelBtnStart = new DigitalInput(Constants.BTN_START);
        panelBtnStop  = new DigitalInput(Constants.BTN_STOP);

        panelLedRun   = new DigitalOutput(Constants.LEDRun);
        panelLedStop  = new DigitalOutput(Constants.LEDStop);
    }

    // =========================================================
    //  Control Panel — Botões físicos
    //  Botões NF (normalmente fechado) → pressionado = false
    //  Detecta borda de descida (soltar o botão)
    // =========================================================
    public enum EstadoRobo {
        PARADO,          // Aguardando início
        EXPLORANDO,      // Executando exploração
        MOVENDO,         // Executando movimento por distância
        GIRANDO,         // Executando rotação
        EXPLORADO,       // Exploração concluída
        PISCANDO         // Piscando LEDs
    }

    /** Retorna true UMA VEZ quando o botão START for solto */
    public boolean getPanelStartPressed() {
        boolean cur    = panelBtnStart.get();
        boolean result = lastStart && !cur;  // borda de descida
        lastStart      = cur;
        return result;
    }

    /** Retorna true UMA VEZ quando o botão STOP for solto */
    public boolean getPanelStopPressed() {
        boolean cur    = panelBtnStop.get();
        boolean result = lastStop && !cur;
        lastStop       = cur;
        return result;
    }

    // // ── LEDs ─────────────────────────────────────────────────
    public void setLedRun(boolean on)  { panelLedRun.set(on);  }
    public void setLedStop(boolean on) { panelLedStop.set(on); }

    /** Ativa LED verde, apaga vermelho */
    public void indicarRodando() { panelLedRun.set(true);  panelLedStop.set(false); }

    /** Ativa LED vermelho, apaga verde */
    public void indicarParado()  { panelLedRun.set(false); panelLedStop.set(true);  }
    // ── Piscar LED Run ────────────────────────────────────────
    private boolean ledRunState = false;
    private int     ciclosPiscar = 0;
    private static final int INTERVALO_PISCAR = 25; // ~500ms a 50Hz

    /** Pisca o LED de run — chame todo periodic quando quiser piscar */
    public void piscarLedRun() {
        ciclosPiscar++;
        if (ciclosPiscar >= INTERVALO_PISCAR) {
            ciclosPiscar  = 0;
            ledRunState   = !ledRunState;
            panelLedRun.set(ledRunState);
        }
    }

    /** Para de piscar e apaga */
    public void pararPiscar() {
        ciclosPiscar = 0;
        ledRunState  = false;
        panelLedRun.set(false);
    }

}