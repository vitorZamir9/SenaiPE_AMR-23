package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class ExecutarGiro extends Command {

    private final DriveSubsystem drive;
    private final double         grausAlvo;

    public ExecutarGiro(double grausAlvo) {
        this.drive     = RobotContainer.drive;
        this.grausAlvo = grausAlvo;
        requires(drive);
    }

    @Override
    protected void initialize() { drive.resetBlocos(); }

    @Override
    protected void execute() { drive.girarParaAngulo(grausAlvo); }
    
    @Override
    protected boolean isFinished() { return drive.giroConcluido; }

    @Override
    protected void end() { drive.stopMotors(); drive.resetBlocos(); }

    @Override
    protected void interrupted() { end(); }
}