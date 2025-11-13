package org.ironmaple.simulation.seasonspecific.heroheist;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.dyn4j.geometry.Circle;
import org.ironmaple.simulation.gamepieces.GamePieceOnFieldSimulation;

/**
 *
 *
 * <h1>Represents a RED STORY PANEL in the 2025 WCP Cadathon FRC Game: Hero Heist game.</h1>
 *
 * <p>The Story Panel is a disk, featured as a game piece in the 2025 WCP Cadathon FRC Game: Hero Heist game.
 *
 * <p>STORY PANELS are 24” diameter, ½” thick, and have a knob on the end. Each STORY PANEL weighs 2.75 lbs.
 */
public class HeroHeistRedStoryPanelOnField extends GamePieceOnFieldSimulation {
    public static final GamePieceInfo HERO_HEIST_RED_STORY_PANEL_INFO =
            new GamePieceInfo("RedStoryPanel", new Circle(0.3048), Meters.of(0), Kilograms.of(1.247379), 2.8, 4, 0.3);

    public HeroHeistRedStoryPanelOnField(Pose2d initialPose) {
        super(HERO_HEIST_RED_STORY_PANEL_INFO, initialPose);
    }

    public HeroHeistRedStoryPanelOnField(Translation2d initialPose) {
        this(new Pose2d(initialPose, Rotation2d.kZero));
    }
}
