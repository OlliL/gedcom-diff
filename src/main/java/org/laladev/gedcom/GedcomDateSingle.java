package org.laladev.gedcom;

public class GedcomDateSingle {
	private boolean before;
	private boolean after;
	private boolean about;
	private boolean calculated;
	private boolean estimated;
	private boolean from;
	private boolean to;
	private boolean interpreted;

	private int day;
	private int month;
	private int year;

	public boolean isBefore() {
		return before;
	}

	public void setBefore(boolean before) {
		this.before = before;
	}

	public boolean isAfter() {
		return after;
	}

	public void setAfter(boolean after) {
		this.after = after;
	}

	public boolean isAbout() {
		return about;
	}

	public void setAbout(boolean about) {
		this.about = about;
	}

	public boolean isCalculated() {
		return calculated;
	}

	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}

	public boolean isEstimated() {
		return estimated;
	}

	public void setEstimated(boolean estimated) {
		this.estimated = estimated;
	}

	public boolean isFrom() {
		return from;
	}

	public void setFrom(boolean from) {
		this.from = from;
	}

	public boolean isTo() {
		return to;
	}

	public void setTo(boolean to) {
		this.to = to;
	}

	public boolean isInterpreted() {
		return interpreted;
	}

	public void setInterpreted(boolean interpreted) {
		this.interpreted = interpreted;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (about ? 1231 : 1237);
		result = prime * result + (after ? 1231 : 1237);
		result = prime * result + (before ? 1231 : 1237);
		result = prime * result + (calculated ? 1231 : 1237);
		result = prime * result + day;
		result = prime * result + (estimated ? 1231 : 1237);
		result = prime * result + (from ? 1231 : 1237);
		result = prime * result + (interpreted ? 1231 : 1237);
		result = prime * result + month;
		result = prime * result + (to ? 1231 : 1237);
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GedcomDateSingle other = (GedcomDateSingle) obj;
		if (about != other.about) {
			return false;
		}
		if (after != other.after) {
			return false;
		}
		if (before != other.before) {
			return false;
		}
		if (calculated != other.calculated) {
			return false;
		}
		if (day != other.day) {
			return false;
		}
		if (estimated != other.estimated) {
			return false;
		}
		if (from != other.from) {
			return false;
		}
		if (interpreted != other.interpreted) {
			return false;
		}
		if (month != other.month) {
			return false;
		}
		if (to != other.to) {
			return false;
		}
		if (year != other.year) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GedcomDate [before=");
		builder.append(before);
		builder.append(", after=");
		builder.append(after);
		builder.append(", about=");
		builder.append(about);
		builder.append(", calculated=");
		builder.append(calculated);
		builder.append(", estimated=");
		builder.append(estimated);
		builder.append(", from=");
		builder.append(from);
		builder.append(", to=");
		builder.append(to);
		builder.append(", interpreted=");
		builder.append(interpreted);
		builder.append(", day=");
		builder.append(day);
		builder.append(", month=");
		builder.append(month);
		builder.append(", year=");
		builder.append(year);
		builder.append("]");
		return builder.toString();
	}

}
