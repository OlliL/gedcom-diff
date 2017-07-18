package org.laladev.gedcom;

public class GedcomDate {
	private GedcomDateSingle date1;
	private GedcomDateSingle date2;
	private String raw;

	public GedcomDate(String raw) {
		super();
		this.raw = raw;

	}

	public GedcomDateSingle getDate1() {
		return date1;
	}

	public void setDate1(GedcomDateSingle date1) {
		this.date1 = date1;
	}

	public GedcomDateSingle getDate2() {
		return date2;
	}

	public void setDate2(GedcomDateSingle date2) {
		this.date2 = date2;
	}

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GedcomDate [date1=");
		builder.append(date1);
		builder.append(", date2=");
		builder.append(date2);
		builder.append(", raw=");
		builder.append(raw);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date1 == null) ? 0 : date1.hashCode());
		result = prime * result + ((date2 == null) ? 0 : date2.hashCode());
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
		GedcomDate other = (GedcomDate) obj;
		if (date1 == null) {
			if (other.date1 != null) {
				return false;
			}
		} else if (!date1.equals(other.date1)) {
			return false;
		}
		if (date2 == null) {
			if (other.date2 != null) {
				return false;
			}
		} else if (!date2.equals(other.date2)) {
			return false;
		}
		return true;
	}

}
