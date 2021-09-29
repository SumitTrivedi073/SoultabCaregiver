package com.soultabcaregiver.companion;

import org.jetbrains.annotations.NotNull;

public enum OnDemandVisitStatus {
	AWAITING_CONFIRMATION {
		@NotNull
		@Override
		public String toString() {
			return "awaiting_confirmation";
		}
	}, IN_PROGRESS {
		@NotNull
		@Override
		public String toString() {
			return "confirmed";
		}
	}, CANCELLED {
		@NotNull
		@Override
		public String toString() {
			return "cancelled";
		}
	}, COMPLETED {
		@NotNull
		@Override
		public String toString() {
			return "completed";
		}
	}, HOLD {
		@NotNull
		@Override
		public String toString() {
			return "hold";
		}
	}, PENDING {
		@NotNull
		@Override
		public String toString() {
			return "pending";
		}
	}
	
	
}
