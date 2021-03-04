package org.egov.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseInfoWrapper {
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("ResponseBody")
	private Object responseBody;
}
