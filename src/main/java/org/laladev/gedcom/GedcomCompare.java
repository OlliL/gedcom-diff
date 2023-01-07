package org.laladev.gedcom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyChild;
import org.gedcom4j.model.FamilySpouse;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.IndividualReference;
import org.gedcom4j.model.Place;
import org.gedcom4j.model.StringWithCustomFacts;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;

public class GedcomCompare {
	private final File file1;
	private final File file2;
	private final Logger logger;
	private final IndividualPrinter individualPrinter;

	public GedcomCompare(final File file1, final File file2, final Logger logger) {
		super();
		this.file1 = file1;
		this.file2 = file2;
		this.logger = logger;
		this.individualPrinter = new IndividualPrinter(logger);
	}

	private static boolean checkIfSameIndividualName(final Individual i1, final Individual i2) {
		final String name1 = i1.getNames().get(0).getBasic().toUpperCase();
		final String name2 = i2.getNames().get(0).getBasic().toUpperCase();
		return name1.equals(name2);
	}

	private static int getLevenshteinDistanceForName(final Individual i1, final Individual i2) {
		final String name1 = i1.getNames().get(0).getBasic().toUpperCase();
		final String name2 = i2.getNames().get(0).getBasic().toUpperCase();
		final Integer distance = LevenshteinDistance.getDefaultInstance().apply(name1, name2);

		return distance.intValue();
	}

	private static boolean checkIfSameIndividualSex(final Individual i1, final Individual i2,
			final List<String> messages) {
		final StringWithCustomFacts sex1 = i1.getSex();
		final StringWithCustomFacts sex2 = i2.getSex();
		if (sex1 == null) {
			if (sex2 != null) {
				messages.add(i1.getNames().get(0).getBasic() + " Sex: (null)");
				messages.add(i2.getNames().get(0).getBasic() + " Sex: " + sex2.getValue());
				return false;
			}
		} else if (sex2 != null) {
			if (!sex1.getValue().equals(sex2.getValue())) {
				messages.add(i1.getNames().get(0).getBasic() + " Sex: " + sex1.getValue());
				messages.add(i2.getNames().get(0).getBasic() + " Sex: " + sex2.getValue());
				return false;
			}
		} else {
			messages.add(i1.getNames().get(0).getBasic() + " Sex: " + sex1.getValue());
			messages.add(i2.getNames().get(0).getBasic() + " Sex: (null)");
			return false;
		}
		return true;
	}

	private static boolean checkIfSameIndividualBasics(final Individual i1, final Individual i2,
			final List<String> messages) {

		final boolean ret = checkIfSameIndividualName(i1, i2);
		if (!ret) {
			messages.add(i1.getNames().get(0).getBasic() + " vs. " + i2.getNames().get(0).getBasic());
		}
		return ret && checkIfSameIndividualEvents(i1, i2, messages) && checkIfSameIndividualSex(i1, i2, messages);
	}

	private static boolean checkIfSameIndividualEvents(final Individual i1, final Individual i2,
			final List<String> messages) {
		final List<IndividualEvent> events1 = i1.getEvents();
		final List<IndividualEvent> events2 = i2.getEvents();

		boolean ret = false;
		if (events1 == null && events2 != null) {
			for (final IndividualEvent individualEvent : events2) {
				messages.add(i2.getNames().get(0).getBasic() + " Event: " + individualEvent.getType());
			}
		} else if (events2 == null && events1 != null) {
			for (final IndividualEvent individualEvent : events1) {
				messages.add(i1.getNames().get(0).getBasic() + " Event: " + individualEvent.getType());
			}
		} else if ((events1 == null && events2 == null) || (events1.isEmpty() && events2.isEmpty())) {
			ret = true;
		} else {
			ret = true;
			for (final IndividualEvent individualEvent1 : events1) {
				boolean check = false;
				for (final IndividualEvent individualEvent2 : events2) {
					check = checkIfSameIndividualEventType(individualEvent1, individualEvent2)
							&& compareDate(individualEvent1.getDate(), individualEvent2.getDate())
							&& comparePlace(individualEvent1.getPlace(), individualEvent2.getPlace());
					if (check) {
						break;
					}
				}
				if (!check) {
					messages.add(i1.getNames().get(0).getBasic() + " Event: " + individualEvent1.getType());
					ret = false;
				}
			}
		}
		return ret;
	}

	private static boolean checkIfSameIndividualEventType(final IndividualEvent individualEvent1,
			final IndividualEvent individualEvent2) {
		final IndividualEventType type1 = individualEvent1.getType();
		final IndividualEventType type2 = individualEvent2.getType();
		return (type1.equals(IndividualEventType.CHRISTENING) && type2.equals(IndividualEventType.BAPTISM))
				|| (type2.equals(IndividualEventType.CHRISTENING) && type1.equals(IndividualEventType.BAPTISM))
				|| type1.equals(type2);
	}

	private static boolean compareDate(final StringWithCustomFacts date1, final StringWithCustomFacts date2) {
		if (date1 == null) {
			if (date2 != null) {
				return false;
			}
		} else if (date2 != null) {
			final GedcomDate gedcomDate1 = GedcomDateParser.parseDate(date1.getValue());
			final GedcomDate gedcomDate2 = GedcomDateParser.parseDate(date2.getValue());

			return gedcomDate1.equals(gedcomDate2);
		}
		return true;
	}

	private static boolean comparePlace(final Place place1, final Place place2) {
		if ((place1 == null && place2 != null) || (place2 == null && place1 != null)) {
			return false;
		} else if (place1 == null && place2 == null) {
			return true;
		} else if ((place1.getPlaceName() == null && place2.getPlaceName() != null)
				|| (place2.getPlaceName() == null && place1.getPlaceName() != null)) {
			return false;
		} else if (place1.getPlaceName() == null && place2.getPlaceName() == null) {
			return true;
		}

		return place1.getPlaceName().contains(place2.getPlaceName())
				|| place2.getPlaceName().contains(place1.getPlaceName());
	}

	private static boolean checkIfSameIndividualFamilies(final Individual i1, final Individual i2,
			final List<String> messages) {
		final List<FamilyChild> familiesWhereChild1 = i1.getFamiliesWhereChild();
		final List<FamilyChild> familiesWhereChild2 = i2.getFamiliesWhereChild();
		if (familiesWhereChild1 != null && familiesWhereChild2 == null) {
			messages.add("File1: parents exist File2: no parents");
			return false;
		} else if (familiesWhereChild2 != null && familiesWhereChild1 == null) {
			messages.add("File2: parents exist File1: no parents");
			return false;
		} else if (familiesWhereChild1 != null && familiesWhereChild2 != null) {
			final FamilyChild familyChild1 = familiesWhereChild1.get(0);
			final FamilyChild familyChild2 = familiesWhereChild2.get(0);
			if (familyChild1 == null && familyChild2 != null || familyChild2 == null && familyChild1 != null) {
				return false;
			} else if (familyChild1 != null && familyChild2 != null) {
				if (!checkIfSameFamily(familyChild1.getFamily(), familyChild2.getFamily(), messages)) {
					return false;
				}
			} else if (familyChild1 != null && familyChild2 == null) {
				messages.add("File1: parents exist File2: no parents");
			} else if (familyChild2 != null && familyChild1 == null) {
				messages.add("File2: parents exist File1: no parents");
			}
		}

		final List<FamilySpouse> familiesWhereSpouse1 = i1.getFamiliesWhereSpouse();
		final List<FamilySpouse> familiesWhereSpouse2 = i2.getFamiliesWhereSpouse();
		final boolean ret = checkIfSameFamilySpouse(familiesWhereSpouse1, familiesWhereSpouse2, messages)
				&& checkIfSameFamilySpouse(familiesWhereSpouse2, familiesWhereSpouse1, messages);
		return ret;
	}

	private static boolean checkIfSameFamilySpouse(final List<FamilySpouse> familiesWhereSpouse1,
			final List<FamilySpouse> familiesWhereSpouse2, final List<String> messages) {
		if (familiesWhereSpouse1 == null && familiesWhereSpouse2 != null
				|| familiesWhereSpouse2 == null && familiesWhereSpouse1 != null) {
			return false;
		} else if ((familiesWhereSpouse1 == null && familiesWhereSpouse2 == null)
				|| (familiesWhereSpouse1.isEmpty() && familiesWhereSpouse2.isEmpty())) {
			return true;
		}

		boolean ret = false;
		for (final FamilySpouse familySpouse1 : familiesWhereSpouse1) {
			ret = false;
			for (final FamilySpouse familySpouse2 : familiesWhereSpouse2) {
				if (checkIfSameFamily(familySpouse1.getFamily(), familySpouse2.getFamily(), messages)) {
					ret = true;
					break;
				}
			}
			if (!ret) {
				break;
			}
		}
		return ret;

	}

	private static boolean checkIfSameFamily(final Family family1, final Family family2, final List<String> messages) {
		if (family1 == null && family2 == null) {
			return true;
		} else if (family1 != null && family2 != null) {
			boolean ret = true;

			if (family1.getHusband() != null && family2.getHusband() == null) {
				messages.add("File 1: " + family1.getHusband().getIndividual().getNames().get(0).getBasic()
						+ " File2: empty");
				ret = false;
			}
			if (family2.getHusband() != null && family1.getHusband() == null) {
				messages.add("File 2: " + family2.getHusband().getIndividual().getNames().get(0).getBasic()
						+ " File1: empty");
				ret = false;
			}

			if (family1.getWife() != null && family2.getWife() == null) {
				messages.add(
						"File 1: " + family1.getWife().getIndividual().getNames().get(0).getBasic() + " File2: empty");
				ret = false;
			}
			if (family2.getWife() != null && family1.getWife() == null) {
				messages.add(
						"File 2: " + family2.getWife().getIndividual().getNames().get(0).getBasic() + " File1: empty");
				ret = false;
			}

			if (family1.getHusband() != null && family2.getHusband() != null) {
				final int distance1 = getLevenshteinDistanceForName(family1.getHusband().getIndividual(),
						family2.getHusband().getIndividual());
				if (family2.getWife() == null || distance1 == 0) {
					ret &= checkIfSameIndividualReference(family1.getHusband(), family2.getHusband(), messages);
				} else {
					final int distance2 = getLevenshteinDistanceForName(family1.getHusband().getIndividual(),
							family2.getWife().getIndividual());
					if (distance2 < 5 && distance2 < distance1) {
						messages.add("Husband (" + family1.getHusband().getIndividual().getNames().get(0).getBasic()
								+ ") and Wife (" + family2.getWife().getIndividual().getNames().get(0).getBasic()
								+ ") switched?");
						ret = false;
					} else {
						ret &= checkIfSameIndividualReference(family1.getHusband(), family2.getHusband(), messages);
					}
				}
			}

			if (family1.getWife() != null && family2.getWife() != null) {
				final int distance1 = getLevenshteinDistanceForName(family1.getWife().getIndividual(),
						family2.getWife().getIndividual());
				if (family2.getHusband() == null || distance1 == 0) {
					ret &= checkIfSameIndividualReference(family1.getWife(), family2.getWife(), messages);
				} else {
					final int distance2 = getLevenshteinDistanceForName(family1.getWife().getIndividual(),
							family2.getHusband().getIndividual());
					if (distance2 < 5 && distance2 < distance1) {
						messages.add("Wife (" + family1.getWife().getIndividual().getNames().get(0).getBasic()
								+ ") and Husband (" + family2.getHusband().getIndividual().getNames().get(0).getBasic()
								+ ") switched?");
						ret = false;
					} else {
						ret &= checkIfSameIndividualReference(family1.getWife(), family2.getWife(), messages);
					}
				}
			}

			return ret;
		}
		return false;
	}

	private static boolean checkIfSameIndividualReference(final IndividualReference individualReference1,
			final IndividualReference individualReference2, final List<String> messages) {
		if (individualReference1 == null && individualReference2 == null) {
			return true;
		} else if (individualReference1 != null && individualReference2 != null) {
			return checkIfSameIndividualBasics(individualReference1.getIndividual(),
					individualReference2.getIndividual(), messages);
		}
		return false;
	}

	public void compareFilesAndLogDifferences() throws IOException, GedcomParserException {
		final GedcomParser gp = new GedcomParser();

		gp.load(this.file1.getPath());
		final Gedcom g1 = gp.getGedcom();

		gp.load(this.file2.getPath());
		final Gedcom g2 = gp.getGedcom();

		int differences = 0;

		for (final Individual i1 : g1.getIndividuals().values()) {
			boolean finalResult = false;
			final List<Individual> bestMatch = new ArrayList<>();
			final List<String> messages = new ArrayList<>();
			for (final Individual i2 : g2.getIndividuals().values()) {
				boolean result = checkIfSameIndividualName(i1, i2);
				if (result) {
					result = checkIfSameIndividualEvents(i1, i2, messages);
					if (result) {
						finalResult = checkIfSameIndividualFamilies(i1, i2, messages);
						if (finalResult) {
							break;
						}
					}
					bestMatch.add(i2);
				}
			}
			if (!finalResult && !bestMatch.isEmpty()) {
				this.logger.append("Datei 1:");
				this.logger.append(
						"------------------------------------------------------------------------------------------------");
				this.individualPrinter.print(i1);
				this.logger.append(
						"------------------------------------------------------------------------------------------------");
				this.logger.append("Datei 2:");
				for (final Individual individual : bestMatch) {
					this.logger.append(
							"------------------------------------------------------------------------------------------------");
					this.individualPrinter.print(individual);
				}
				this.logger.append(
						"------------------------------------------------------------------------------------------------");
				for (final String message : messages) {
					this.logger.append(message);
				}
				this.logger.append(
						"================================================================================================");
				differences++;
			} else if (!finalResult) {
				this.logger.append("Datei 1:");
				this.logger.append(
						"------------------------------------------------------------------------------------------------");
				this.individualPrinter.print(i1);
				this.logger.append(
						"------------------------------------------------------------------------------------------------");
				this.logger.append("Datei 2:");
				this.logger.append(
						"------------------------------------------------------------------------------------------------");
				this.logger.append("not found");
				this.logger.append(
						"================================================================================================");
				differences++;
			}
		}

		this.logger.append(differences + " differences found!");
	}

}
