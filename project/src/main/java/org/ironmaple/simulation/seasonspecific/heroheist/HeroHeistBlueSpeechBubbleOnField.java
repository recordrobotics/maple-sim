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
 * <h1>Represents a BLUE SPEECH BUBBLE in the 2025 WCP Cadathon FRC Game: Hero Heist game.</h1>
 *
 * <p>SPEECH BUBBLES are 7” diameter foam balls reminiscent of the “power cell” game piece from the 2020 FRC game,
 * Infinite Recharge. SPEECH BUBBLES come in red and blue colors and weigh approximately 5 oz.
 */
public class HeroHeistBlueSpeechBubbleOnField extends GamePieceOnFieldSimulation {
    public static final GamePieceInfo HERO_HEIST_BLUE_SPEECH_BUBBLE_INFO = new GamePieceInfo(
            "BlueSpeechBubble", new Circle(0.0889), Inches.of(7), Kilograms.of(0.141748), 1.8, 5, 0.8);

    public HeroHeistBlueSpeechBubbleOnField(Pose2d initialPose) {
        super(HERO_HEIST_BLUE_SPEECH_BUBBLE_INFO, initialPose);
    }

    public HeroHeistBlueSpeechBubbleOnField(Translation2d initialPose) {
        this(new Pose2d(initialPose, Rotation2d.kZero));
    }
}
