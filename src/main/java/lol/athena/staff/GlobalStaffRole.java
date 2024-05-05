package lol.athena.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GlobalStaffRole {
	OWNER(0),
	MANAGER(1),
	ADMINISTRATOR(2),
	MODERATOR(3);
	
	/**
	 * Lower Number = Higher Priority
	 */
	@Getter private final int rolePriority;
}
