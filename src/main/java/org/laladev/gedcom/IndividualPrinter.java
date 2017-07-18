package org.laladev.gedcom;

import java.util.List;

import org.gedcom4j.model.FamilyChild;
import org.gedcom4j.model.FamilyEvent;
import org.gedcom4j.model.FamilySpouse;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.IndividualReference;

public class IndividualPrinter {
	private static final String PREFIX_1_LEVEL = "        ";
	private static final String PREFIX_2_LEVEL = PREFIX_1_LEVEL + PREFIX_1_LEVEL;
	private final Logger logger;

	public IndividualPrinter(final Logger logger) {
		super();
		this.logger = logger;
	}

	public void print(final Individual i) {
		this.printName(i);
		this.printSex(i);
		this.printIndividualEvents(i.getEvents());

		final List<FamilyChild> familiesWhereChild = i.getFamiliesWhereChild();
		if (familiesWhereChild != null) {
			final FamilyChild familyChild = familiesWhereChild.get(0);

			final IndividualReference husband = familyChild.getFamily().getHusband();
			final IndividualReference wife = familyChild.getFamily().getWife();

			if (husband != null) {
				this.logger.append("-- Vater ------------------------------------------");
				this.printName(husband.getIndividual(), PREFIX_1_LEVEL);
				this.printIndividualEvents(husband.getIndividual().getEvents(), PREFIX_2_LEVEL);
			}
			if (wife != null) {
				this.logger.append("-- Mutter -----------------------------------------");
				this.printName(wife.getIndividual(), PREFIX_1_LEVEL);
				this.printIndividualEvents(wife.getIndividual().getEvents(), PREFIX_2_LEVEL);
			}
		}

		final List<FamilySpouse> familiesWhereSpouse = i.getFamiliesWhereSpouse();
		if (familiesWhereSpouse != null) {
			for (final FamilySpouse familySpouse : familiesWhereSpouse) {
				if (familySpouse.getFamily() != null) {
					final IndividualReference husband = familySpouse.getFamily().getHusband();
					final IndividualReference wife = familySpouse.getFamily().getWife();
					this.logger.append("-- Ehe --------------------------------------------");
					this.printFamilyEvents(familySpouse.getFamily().getEvents(), PREFIX_1_LEVEL);

					Individual spouse = null;
					if (wife != null && !wife.getIndividual().equals(i)) {
						spouse = wife.getIndividual();
					}
					if (husband != null && !husband.getIndividual().equals(i)) {
						spouse = husband.getIndividual();
					}
					if (spouse != null) {
						this.printName(spouse, PREFIX_1_LEVEL);
						this.printIndividualEvents(spouse.getEvents(), PREFIX_2_LEVEL);
					}
				}
			}
		}

	}

	private void printSex(final Individual i) {
		String sex;
		if (i.getSex() == null) {
			sex = "U";
		} else {
			sex = i.getSex().getValue();
		}
		this.logger.append("Geschlecht: " + sex);

	}

	private void printName(final Individual i) {
		this.printName(i, "");
	}

	private void printName(final Individual i, final String prefix) {
		this.logger.append(prefix + "Name: " + i.getNames().get(0).getBasic());
	}

	private void printIndividualEvents(final List<IndividualEvent> events) {
		this.printIndividualEvents(events, "");
	}

	private void printIndividualEvents(final List<IndividualEvent> events, final String prefix) {
		if (events != null) {
			for (final IndividualEvent event : events) {
				if (event.getPlace() != null) {
					this.logger.append(prefix + event.getType().name() + ": " + event.getDate() + ", "
							+ event.getPlace().getPlaceName());
				} else {
					this.logger.append(prefix + event.getType().name() + ": " + event.getDate());
				}
			}
		}
	}

	private void printFamilyEvents(final List<FamilyEvent> events, final String prefix) {
		if (events != null) {
			for (final FamilyEvent event : events) {
				if (event.getPlace() != null) {
					this.logger.append(prefix + event.getType().name() + ": " + event.getDate() + ", "
							+ event.getPlace().getPlaceName());
				} else {
					this.logger.append(prefix + event.getType().name() + ": " + event.getDate());
				}
			}
		}
	}
}
