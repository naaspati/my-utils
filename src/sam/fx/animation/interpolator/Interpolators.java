package sam.fx.animation.interpolator;

import javafx.animation.Interpolator;

/**
 * Splines defined on 
 * http://easings.net/#
 *
 */
public interface Interpolators {
    public static final double VERSION = 1;
    
	public static final Interpolator EASE_IN_SINE = Interpolator.SPLINE(0.47, 0, 0.745, 0.715);
	public static final Interpolator EASE_OUT_SINE = Interpolator.SPLINE(0.39, 0.575, 0.565, 1);
	public static final Interpolator EASE_IN_OUT_SINE = Interpolator.SPLINE(0.445, 0.05, 0.55, 0.95);
	public static final Interpolator EASE_IN_QUAD = Interpolator.SPLINE(0.55, 0.085, 0.68, 0.53);
	public static final Interpolator EASE_OUT_QUAD = Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94);
	public static final Interpolator EASE_IN_OUT_QUAD = Interpolator.SPLINE(0.455, 0.03, 0.515, 0.955);
	public static final Interpolator EASE_IN_CUBIC = Interpolator.SPLINE(0.55, 0.055, 0.675, 0.19);
	public static final Interpolator EASE_OUT_CUBIC = Interpolator.SPLINE(0.215, 0.61, 0.355, 1);
	public static final Interpolator EASE_IN_OUT_CUBIC = Interpolator.SPLINE(0.645, 0.045, 0.355, 1);
	public static final Interpolator EASE_IN_QUART = Interpolator.SPLINE(0.895, 0.03, 0.685, 0.22);
	public static final Interpolator EASE_OUT_QUART = Interpolator.SPLINE(0.165, 0.84, 0.44, 1);
	public static final Interpolator EASE_IN_OUT_QUART = Interpolator.SPLINE(0.77, 0, 0.175, 1);
	public static final Interpolator EASE_IN_QUINT = Interpolator.SPLINE(0.755, 0.05, 0.855, 0.06);
	public static final Interpolator EASE_OUT_QUINT = Interpolator.SPLINE(0.23, 1, 0.32, 1);
	public static final Interpolator EASE_IN_OUT_QUINT = Interpolator.SPLINE(0.86, 0, 0.07, 1);
	public static final Interpolator EASE_IN_EXPO = Interpolator.SPLINE(0.95, 0.05, 0.795, 0.035);
	public static final Interpolator EASE_OUT_EXPO = Interpolator.SPLINE(0.19, 1, 0.22, 1);
	public static final Interpolator EASE_IN_OUT_EXPO = Interpolator.SPLINE(1, 0, 0, 1);
	public static final Interpolator EASE_IN_CIRC = Interpolator.SPLINE(0.6, 0.04, 0.98, 0.335);
	public static final Interpolator EASE_OUT_CIRC = Interpolator.SPLINE(0.075, 0.82, 0.165, 1);
	public static final Interpolator EASE_IN_OUT_CIRC = Interpolator.SPLINE(0.785, 0.135, 0.15, 0.86);

	/**  not possible with spline
	 * 
	 * 
	 * easeInBack	  0.6, -0.28, 0.735, 0.045
	 * easeOutBack	  0.175, 0.885, 0.32, 1.275
	 * easeInOutBack  0.68, -0.55, 0.265, 1.55
	 * easeInElastic	
	 * easeOutElastic	
	 * easeInOutElastic	
	 * easeInBounce	
	 * easeOutBounce	
	 * easeInOutBounce	
	 */
}
