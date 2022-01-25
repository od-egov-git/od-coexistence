package org.egov.asset.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {

	REVALUATION("Revaluation"),

	SALE("SALE"),

	DISPOSAL("DISPOSAL"),

	DEPRECIATION("DEPRECIATION"),

	CREATE("CREATE");

	private String value;

	TransactionType(final String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static TransactionType fromValue(final String text) {
		for (final TransactionType b : TransactionType.values())
			if (String.valueOf(b.value).equals(text))
				return b;
		return null;
	}
}
