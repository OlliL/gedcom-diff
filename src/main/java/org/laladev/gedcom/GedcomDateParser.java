package org.laladev.gedcom;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * See <a href="http://wiki-de.genealogy.net/GEDCOM/DATE-Tag">DATE Tag</a>.
 *
 * @author Oliver Lehmann
 *
 */
public class GedcomDateParser {
	private final static Map<String, Integer> MONTH_MAP = Collections.unmodifiableMap(new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			this.put("JAN", Calendar.JANUARY);
			this.put("FEB", Calendar.FEBRUARY);
			this.put("MAR", Calendar.MARCH);
			this.put("APR", Calendar.APRIL);
			this.put("MAY", Calendar.MAY);
			this.put("JUN", Calendar.JUNE);
			this.put("JUL", Calendar.JULY);
			this.put("AUG", Calendar.AUGUST);
			this.put("SEP", Calendar.SEPTEMBER);
			this.put("OCT", Calendar.OCTOBER);
			this.put("NOV", Calendar.NOVEMBER);
			this.put("DEC", Calendar.DECEMBER);

			this.put("M\u00c4R", Calendar.MARCH);
			this.put("MRZ", Calendar.MARCH);
			this.put("MAI", Calendar.MAY);
			this.put("OKT", Calendar.OCTOBER);
			this.put("DEZ", Calendar.DECEMBER);
			this.put("SEPT", Calendar.SEPTEMBER);

			this.put("JANUAR", Calendar.JANUARY);
			this.put("FEBRUAR", Calendar.FEBRUARY);
			this.put("M\u00c4RZ", Calendar.MARCH);
			this.put("APRIL", Calendar.APRIL);
			this.put("MAI", Calendar.MAY);
			this.put("JUNI", Calendar.JUNE);
			this.put("JULI", Calendar.JULY);
			this.put("AUGUST", Calendar.AUGUST);
			this.put("SEPTEMBER", Calendar.SEPTEMBER);
			this.put("OKTOBER", Calendar.OCTOBER);
			this.put("NOVEMBER", Calendar.NOVEMBER);
			this.put("DEZEMBER", Calendar.DECEMBER);
		}
	});

	public static GedcomDate parseDate(final String rawDate) {
		final GedcomDate gedcomDate = new GedcomDate(rawDate);

		final String[] split = rawDate.split(" ");
		GedcomDateSingle date = new GedcomDateSingle();

		boolean toSeen = false;

		for (int i = split.length - 1; i >= 0; i--) {
			final String raw = split[i].toUpperCase();

			if (toSeen) {
				gedcomDate.setDate2(date);
				date = new GedcomDateSingle();
				toSeen = false;
			}

			if (isYear(raw)) {
				date.setYear(getYear(raw));
			} else if (isMonth(raw)) {
				date.setMonth(getMonth(raw));
			} else if (isDay(raw)) {
				date.setDay(getDay(raw));
			} else if (isKeyword(raw)) {
				switch (raw) {
				case "BEF":
					date.setBefore(true);
					break;
				case "AFT":
					date.setAfter(true);
					break;
				case "ABT":
					date.setAbout(true);
					break;
				case "CAL":
					date.setCalculated(true);
					break;
				case "EST":
					date.setEstimated(true);
					break;
				case "FROM":
					date.setFrom(true);
					break;
				case "INT":
					date.setInterpreted(true);
					break;
				}
			} else if (isAndKeyword(raw)) {
				gedcomDate.setDate2(date);
				date = new GedcomDateSingle();
			} else if (isToKeyword(raw)) {
				toSeen = true;
				date.setTo(true);
			}
		}

		gedcomDate.setDate1(date);

		return gedcomDate;

	}

	private static boolean isYear(final String raw) {
		return raw.matches("\\d\\d\\d\\d");
	}

	private static boolean isMonth(final String raw) {
		return getMonth(raw) != null;
	}

	private static boolean isDay(final String raw) {
		return raw.matches("\\d\\d?");
	}

	private static boolean isAndKeyword(final String raw) {
		return raw.equals("AND");
	}

	private static boolean isToKeyword(final String raw) {
		return raw.equals("TO");
	}

	private static boolean isKeyword(final String raw) {
		switch (raw) {
		case "BEF":
		case "AFT":
		case "ABT":
		case "CAL":
		case "EST":
		case "FROM":
		case "INT":
			return true;
		default:
			return false;

		}
	}

	private static int getYear(final String raw) {
		return Integer.parseInt(raw);
	}

	private static int getDay(final String raw) {
		return Integer.parseInt(raw);
	}

	private static Integer getMonth(final String raw) {
		return MONTH_MAP.get(raw);
	}
}
