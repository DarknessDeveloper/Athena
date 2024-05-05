package lol.athena.plugin.naming;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class NamespacedKey {

	@Getter private String name;
	@Getter private String value;
	
	@Override
	public String toString() {
		return String.format("%s:%s", name, value);
	}
	
	public boolean equals(NamespacedKey key) {
		return key.getName().equalsIgnoreCase(name) && key.getValue().equalsIgnoreCase(value);
	}
	
	public boolean isContainedWithin(Collection<NamespacedKey> c) {
		for (NamespacedKey key : c) {
			if (key.equals(this)) {
				return true;
			}
		}
		return false;
	}
}
