package swiconsim.test;

import static org.junit.Assert.*;
import swiconsim.flow.Match;
import swiconsim.packet.PacketIdentifier;
import swiconsim.util.IPUtil;

import org.junit.Test;

/**
 * @author praveen
 *
 * Testing match in flow table
 *
 */
public class MatchTest {
	@Test
	public  void test() {
		Match m = new Match((short) 0, IPUtil.stringToIP("1.2.3.4"), 16,
				IPUtil.stringToIP("6.7.8.9"), 0);
		System.out.println(m.toString());
		boolean isMatch = m.isMatch(new PacketIdentifier((short) 1, IPUtil.stringToIP("1.2.3.5"),
				IPUtil.stringToIP("1.1.1.1")));
		assertTrue(isMatch);

		isMatch = m.isMatch(new PacketIdentifier((short) 1, IPUtil.stringToIP("1.3.3.5"),
				IPUtil.stringToIP("1.1.1.1")));
		assertFalse(isMatch);

		m = new Match((short) 1, IPUtil.stringToIP("1.2.3.4"), 16,
				IPUtil.stringToIP("6.7.8.9"), 0);
		System.out.println(m.toString());
		isMatch = m.isMatch(new PacketIdentifier((short) 1, IPUtil.stringToIP("1.2.3.5"),
				IPUtil.stringToIP("1.1.1.1")));
		assertTrue(isMatch);
		isMatch = m.isMatch(new PacketIdentifier((short) 2, IPUtil.stringToIP("1.2.3.5"),
				IPUtil.stringToIP("1.1.1.1")));
		assertFalse(isMatch);
		
	}
}
