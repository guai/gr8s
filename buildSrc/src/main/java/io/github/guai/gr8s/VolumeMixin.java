package io.github.guai.gr8s;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.k8s.api.core.v1.CSIVolumeSource;

abstract public class VolumeMixin {
//	@JsonProperty("iscsi")
//	public abstract ISCSIVolumeSource getIscsi();

	@JsonProperty("csi")
    public abstract CSIVolumeSource getCsi();

}
