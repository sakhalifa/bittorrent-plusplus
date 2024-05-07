package fr.ystat.peer.leecher;

import java.net.InetSocketAddress;
import java.util.Objects;

public class SeederConnection {
	private final InetSocketAddress seederAddress;

	public SeederConnection(InetSocketAddress seederAddress) {
		this.seederAddress = seederAddress;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SeederConnection that = (SeederConnection) o;
		return Objects.equals(seederAddress, that.seederAddress);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(seederAddress);
	}
}
