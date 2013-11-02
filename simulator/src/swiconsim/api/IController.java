package swiconsim.api;

import swiconsim.controller.Topology;

/**
 * @author praveen
 *
 * APIs provided by controller to applications
 * 
 */
public interface IController {
	/**
	 * Get topology
	 * @return
	 */
	Topology getTopology();
}
