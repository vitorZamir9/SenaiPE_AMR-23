package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class MoverDistancia extends Command {

    private final DriveSubsystem drive;
    private final String         direcao;
    private final double         metros;

    public MoverDistancia(String direcao, double metros) {
        this.drive   = RobotContainer.drive;
        this.direcao = direcao;
        this.metros  = metros;
        requires(drive);
    }

    @Override
    protected void initialize() { drive.resetBlocos(); }

    @Override
    protected void execute() { drive.moverDistancia(direcao, metros); }

    @Override
    protected boolean isFinished() { return drive.moveConcluido; }

    @Override
    protected void end() { drive.stopMotors(); drive.resetBlocos(); }

    @Override
    protected void interrupted() { end(); }
}