package org.xpect.tests;

import org.junit.Test;
import org.xpect.runner.XpectSuiteClasses;
import org.xpect.setup.AbstractXpectSetup;
import org.xpect.setup.XpectSetup;
import org.xpect.tests.XjmSetupsTest.SetupsAndSuite.SetupsAndSuiteTest;

public class XjmSetupsTest extends AbstractXjmTest {

	public static class NullSetup extends AbstractXpectSetup<Object, Object, Object, Object> {
	}

	public static class OtherSetup extends AbstractXpectSetup<Object, Object, Object, Object> {
	}

	@XpectSetup({ NullSetup.class })
	public static class SingleSetup {

		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("suite org.xpect.tests.XjmSetupsTest$SingleSetup {\n");
			buf.append("  setup org.xpect.tests.XjmSetupsTest$NullSetup\n");
			buf.append("  test org.xpect.tests.XjmSetupsTest$SingleSetup {\n");
			buf.append("    NullSetup\n");
			buf.append("  }\n");
			buf.append("}");
			return buf.toString();
		}

	}

	@XpectSetup({ NullSetup.class, OtherSetup.class })
	public static class TwoSetups {

		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("suite org.xpect.tests.XjmSetupsTest$TwoSetups {\n");
			buf.append("  setup org.xpect.tests.XjmSetupsTest$NullSetup\n");
			buf.append("  setup org.xpect.tests.XjmSetupsTest$OtherSetup\n");
			buf.append("  test org.xpect.tests.XjmSetupsTest$TwoSetups {\n");
			buf.append("    NullSetup\n");
			buf.append("    OtherSetup\n");
			buf.append("  }\n");
			buf.append("}");
			return buf.toString();
		}

	}

	@XpectSetup({ NullSetup.class, OtherSetup.class })
	@XpectSuiteClasses(SetupsAndSuiteTest.class)
	public static class SetupsAndSuite {

		@XpectSetup({ NullSetup.class, OtherSetup.class })
		public static class SetupsAndSuiteTest {

		}

		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("suite org.xpect.tests.XjmSetupsTest$SetupsAndSuite {\n");
			buf.append("  setup org.xpect.tests.XjmSetupsTest$NullSetup\n");
			buf.append("  setup org.xpect.tests.XjmSetupsTest$OtherSetup\n");
			buf.append("  test org.xpect.tests.XjmSetupsTest$SetupsAndSuite {\n");
			buf.append("    NullSetup\n");
			buf.append("    OtherSetup\n");
			buf.append("  }\n");
			buf.append("  test org.xpect.tests.XjmSetupsTest$SetupsAndSuite$SetupsAndSuiteTest {\n");
			buf.append("    NullSetup\n");
			buf.append("    OtherSetup\n");
			buf.append("  }\n");
			buf.append("}");
			return buf.toString();
		}

	}

	@Test
	public void singleSetup() {
		assertXjm(SingleSetup.class);
	}

	@Test
	public void twoSetups() {
		assertXjm(TwoSetups.class);
	}

	@Test
	public void setupsAndSuite() {
		assertXjm(SetupsAndSuite.class);
	}
}
