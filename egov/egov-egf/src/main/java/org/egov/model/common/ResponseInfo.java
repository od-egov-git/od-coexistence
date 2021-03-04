package org.egov.model.common;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseInfo {
	@JsonProperty("apiId")
	@NotNull
	@Size(max = 128)
	private String apiId = null;

	@JsonProperty("ver")
	@NotNull
	@Size(max = 32)
	private String ver = null;

	@JsonProperty("ts")
	@NotNull
	private Long ts = null;

	@JsonProperty("resMsgId")
	private String resMsgId = null;

	@JsonProperty("msgId")
	@Size(max = 256)
	private String msgId = null;

	@JsonProperty("status")
	@NotNull
	private String status = null;
}
