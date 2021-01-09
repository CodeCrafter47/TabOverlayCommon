/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;
import lombok.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomPlaceholderSelect extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, ExpressionUpdateListener, TextViewUpdateListener {

    private final List<SelectEntry> entryList;
    private final TextTemplate defaultReplacement;
    private TextView activeView;
    private int activeEntry = -1;

    public CustomPlaceholderSelect(Map<ExpressionTemplate, TextTemplate> replacements, TextTemplate defaultReplacement) {
        this.entryList = new ArrayList<>(replacements.size());
        for (Map.Entry<ExpressionTemplate, TextTemplate> entry : replacements.entrySet()) {
            this.entryList.add(new SelectEntry(entry.getKey().instantiateWithBooleanResult(), entry.getValue()));
        }

        this.defaultReplacement = defaultReplacement;
    }

    private void update(boolean fireEvent) {
        if (this.activeView != null) {
            this.activeView.deactivate();
        }

        // find the first entry whose expression evaluates to true
        int i = 0;
        for (; i < this.entryList.size(); i++) {
            // the the expression corresponding to entry i
            ToBooleanExpression expression = this.entryList.get(i).getExpression();

            // if the expression is not active, activate it
            if (i > this.activeEntry) {
                expression.activate(getContext(), this);
            }

            // check the expression, stop if it evaluates to true
            if (expression.evaluate()) {
                break;
            }
        }

        // disable expressions that are no longer needed
        for (int j = i + 1; j <= this.activeEntry && j < this.entryList.size(); j++) {
            this.entryList.get(j).getExpression().deactivate();
        }

        // set the activeEntry to i
        this.activeEntry = i;

        // select the new replacement from the list or the default replacement
        TextTemplate template = i < this.entryList.size()
                ? this.entryList.get(i).getReplacement()
                : this.defaultReplacement;

        // activate the new replacement
        activeView = template.instantiate();
        activeView.activate(getContext(), this);

        if (fireEvent && hasListener()) {
            getListener().run();
        }
    }

    @Override
    protected void onActivation() {
        activeEntry = -1;
        update(false);
    }

    @Override
    protected void onDeactivation() {
        // deactivate the active replacement
        if (activeView != null) {
            activeView.deactivate();
        }

        // disable active expressions
        for (int j = 0; j <= this.activeEntry && j < this.entryList.size(); j++) {
            this.entryList.get(j).getExpression().deactivate();
        }
        this.activeEntry = -1;
    }

    @Override
    public void onExpressionUpdate() {
        update(true);
    }

    @Override
    public void onTextUpdated() {
        if (hasListener()) {
            getListener().run();
        }
    }

    @Override
    public String getData() {
        return activeView.getText();
    }

    @Value
    private static class SelectEntry {
        ToBooleanExpression expression;
        TextTemplate replacement;
    }

}







