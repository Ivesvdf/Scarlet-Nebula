/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ac.ua.comp.scarletnebula.gui.welcomewizard;

import java.awt.Frame;

import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;

public class WelcomeWizard extends Wizard {

	public WelcomeWizard(final Frame parent) {
		super(new ThanksPage(), null, new SimpleWizardTemplate());

		startModal("Thanks for trying Scarlet Nebula", 400, 310, parent);
	}

}
