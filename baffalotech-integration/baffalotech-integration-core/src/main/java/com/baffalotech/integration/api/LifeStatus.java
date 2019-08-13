package com.baffalotech.integration.api;

/**
 * 状态，启动中，已启动，终止中，已终止
 * @author chijinhuang
 *
 */
public enum LifeStatus {

	INIT,
	STARTING,
	STARTED,
	STOPPING,
	STOPPED
}
