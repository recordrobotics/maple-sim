package org.ironmaple.simulation.motorsims;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.ironmaple.simulation.SimulatedArena;

/**
 *
 *
 * <h1>Simulates the main battery of the robot.</h1>
 *
 * <p>This class simulates the behavior of a robot's battery. Electrical appliances can be added to the battery to draw
 * current. The battery voltage is affected by the current drawn from various appliances.
 */
public class SimulatedBattery {

    public static final SimulatedBattery ROBORIO_BATTERY = new SimulatedBattery();

    // Nominal voltage for a fully charged battery (13.5 volts).
    private static final double DEFAULT_BATTERY_NOMINAL_VOLTAGE = 13.5;

    // Filter to smooth the current readings.
    private LinearFilter currentFilter = LinearFilter.movingAverage(50);

    private final List<Supplier<Current>> electricalAppliances = new ArrayList<>();

    // The current battery voltage in volts.
    private double currentChargeVoltage = DEFAULT_BATTERY_NOMINAL_VOLTAGE;
    private double batteryVoltageVolts = DEFAULT_BATTERY_NOMINAL_VOLTAGE;
    private double batteryInternalResistance = 0.02;

    private double timeOfLastVoltageLow = -1;
    private double voltageSagThreshold = 0.95;
    private Pair<Double, Double> voltageSagCoefficients = new Pair<>(1.70518, 2.81719);

    private double dischargeRate = 0.05 / 378.45; // Volts per amp-second
    private Pair<Double, Double> dischargeCoefficients = new Pair<>(13.48393, 1.34388);

    /**
     *
     *
     * <h2>Sets the current filter for the battery simulation.</h2>
     *
     * <p>By default a moving average filter with 50 samples is used
     *
     * @param filter The filter to smooth the current readings
     */
    public void setCurrentFilter(LinearFilter filter) {
        currentFilter = filter;
    }

    /**
     *
     *
     * <h2>Sets the current charge voltage of the battery.</h2>
     *
     * <p>This method can be used to set an initial voltage for the simulation.
     *
     * @param voltage The voltage to set the battery to, in volts.
     */
    public void setVoltage(double voltage) {
        currentChargeVoltage = voltage;
    }

    /**
     *
     *
     * <h2>Sets the internal resistance of the battery.</h2>
     *
     * @param resistance The internal resistance to set, in ohms.
     */
    public void setBatteryInternalResistance(double resistance) {
        batteryInternalResistance = resistance;
    }

    /**
     *
     *
     * <h2>Sets the voltage sag threshold for the battery simulation.</h2>
     *
     * <blockquote>
     *
     * <p>The voltage sag threshold is the ratio of the loaded battery voltage to the current charge voltage below which
     * the battery is considered to be in a voltage sag condition. For example, a threshold of 0.95 means that if the
     * loaded battery voltage drops below 95% of the current charge voltage, it will be considered a voltage sag.
     *
     * </blockquote>
     *
     * @param threshold The voltage sag threshold to set.
     */
    public void setVoltageSagThreshold(double threshold) {
        voltageSagThreshold = threshold;
    }

    /**
     *
     *
     * <h2>Sets the voltage sag coefficients (positive) for the battery simulation.</h2>
     *
     * <p>The voltage sag is calculated using the formula: coefficient1 / (timeSinceVoltageLow + coefficient2).
     *
     * @param coefficients The voltage sag coefficients to set.
     */
    public void setVoltageSagCoefficients(Pair<Double, Double> coefficients) {
        voltageSagCoefficients = coefficients;
    }

    /**
     *
     *
     * <h2>Sets the discharge rate for the battery simulation in volts per amp-second.</h2>
     *
     * @param rate The discharge rate to set.
     */
    public void setDischargeRate(double rate) {
        dischargeRate = rate;
    }

    /**
     *
     *
     * <h2>Sets the discharge coefficients for the battery simulation.</h2>
     *
     * <p>The discharge fraction is calculated using the formula: 1 / (voltage - coefficient1) + coefficient2.
     *
     * @param coefficients The discharge coefficients to set.
     */
    public void setDischargeCoefficients(Pair<Double, Double> coefficients) {
        dischargeCoefficients = coefficients;
    }

    /**
     *
     *
     * <h2>Adds a custom electrical appliance.</h2>
     *
     * <p>Connects the electrical appliance to the battery, allowing it to draw current from the battery.
     *
     * @param customElectricalAppliances The supplier for the current drawn by the appliance.
     */
    public void addElectricalAppliances(Supplier<Current> customElectricalAppliances) {
        electricalAppliances.add(customElectricalAppliances);
    }

    /**
     *
     *
     * <h2>Adds a motor to the list of electrical appliances.</h2>
     *
     * <p>The motor will draw current from the battery.
     *
     * @param mapleMotorSim The motor simulation object.
     */
    public void addMotor(MapleMotorSim mapleMotorSim) {
        electricalAppliances.add(mapleMotorSim::getSupplyCurrent);
    }

    /**
     *
     *
     * <h2>Updates the battery simulation.</h2>
     *
     * <p>Calculates the battery voltage based on the current drawn by all appliances.
     *
     * <p>The battery voltage is clamped to avoid going below the brownout voltage.
     */
    public void simulationSubTick() {
        double totalCurrentAmps = getTotalCurrentDrawn().in(Amps);
        double filteredTotalCurrentAmps = currentFilter.calculate(totalCurrentAmps);

        // Discharge
        currentChargeVoltage = MathUtil.clamp(
                currentChargeVoltage
                        - dischargeRate
                                * Math.max(
                                        0.1,
                                        1 / (currentChargeVoltage - dischargeCoefficients.getFirst())
                                                + dischargeCoefficients.getSecond())
                                * totalCurrentAmps
                                * SimulatedArena.getSimulationDt().in(Seconds),
                RoboRioSim.getBrownoutVoltage(),
                DEFAULT_BATTERY_NOMINAL_VOLTAGE);

        batteryVoltageVolts = BatterySim.calculateLoadedBatteryVoltage(
                currentChargeVoltage - calculateVoltageSag(), batteryInternalResistance, filteredTotalCurrentAmps);

        if (Double.isNaN(batteryVoltageVolts)) {
            batteryVoltageVolts = 12.0;
            DriverStation.reportError(
                    "[MapleSim] Internal Library Error: Calculated battery voltage is invalid"
                            + ", reverting to normal operation voltage...",
                    false);
        }
        if (batteryVoltageVolts < RoboRioSim.getBrownoutVoltage()) {
            batteryVoltageVolts = RoboRioSim.getBrownoutVoltage();
            DriverStation.reportError("[MapleSim] BrownOut Detected, protecting battery voltage...", false);
        }

        if (batteryVoltageVolts / currentChargeVoltage < voltageSagThreshold) {
            timeOfLastVoltageLow = Timer.getTimestamp();
        }

        if (this == ROBORIO_BATTERY) {
            RoboRioSim.setVInVoltage(batteryVoltageVolts);

            SmartDashboard.putNumber("BatterySim/TotalCurrent (Amps)", filteredTotalCurrentAmps);
            SmartDashboard.putNumber("BatterySim/BatteryVoltage (Volts)", batteryVoltageVolts);
        }
    }

    private double calculateVoltageSag() {
        if (timeOfLastVoltageLow < 0) {
            return 0;
        } else {
            double timeSinceVoltageLow = Timer.getTimestamp() - timeOfLastVoltageLow;
            return voltageSagCoefficients.getFirst() / (timeSinceVoltageLow + voltageSagCoefficients.getSecond());
        }
    }

    /**
     *
     *
     * <h2>Obtains the voltage of the battery.</h2>
     *
     * @return The battery voltage as a {@link Voltage} object.
     */
    public Voltage getBatteryVoltage() {
        return Volts.of(batteryVoltageVolts);
    }

    /**
     *
     *
     * <h2>Obtains the total current drawn from the battery.</h2>
     *
     * <p>Iterates through all the appliances to obtain the total current used.
     *
     * @return The total current as a {@link Current} object.
     */
    public Current getTotalCurrentDrawn() {
        double totalCurrentAmps = electricalAppliances.stream()
                .mapToDouble(currentSupplier -> currentSupplier.get().in(Amps))
                .sum();
        return Amps.of(totalCurrentAmps);
    }

    /**
     *
     *
     * <h2>Clamps the voltage according to the supplied voltage and the battery's capabilities.</h2>
     *
     * <p>If the supplied voltage exceeds the battery's maximum voltage, it will be reduced to match the battery's
     * voltage.
     *
     * @param voltage The voltage to be clamped.
     * @return The clamped voltage as a {@link Voltage} object.
     */
    public Voltage clamp(Voltage voltage) {
        return Volts.of(MathUtil.clamp(voltage.in(Volts), -batteryVoltageVolts, batteryVoltageVolts));
    }
}
