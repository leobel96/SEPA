/* This interface includes all the methods that need to be implemented by a SEPA listener
 * 
 * Author: Luca Roffia (luca.roffia@unibo.it)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package it.unibo.arces.wot.sepa.api;

import it.unibo.arces.wot.sepa.commons.response.ErrorResponse;
import it.unibo.arces.wot.sepa.commons.response.Notification;

/**
 * Handles SPARQL 1.1 subscription language events.
 *
 * @see SPARQL11SEProtocol#SPARQL11SEProtocol(SPARQL11SEProperties, ISubscriptionHandler)
 * @see <a href="http://wot.arces.unibo.it/TR/sparql11-subscribe.html">SPARQL 1.1 Subscribe Language</a>
 */
public interface ISubscriptionHandler {
	/**
	 * An event about changes in the graph.
	 * @param notify notification about added and removed triples
	 */
	void onSemanticEvent(Notification notify);

	/**
	 * SEPA sends periodic messages to check client status.
	 */
	void onPing();

	/**
	 * This method is called after the connection with SEPA is lost.
	 * Notice that it is also called even after {@link SEPAWebsocketClient#close()}
	 * is used.
	 */
	void onBrokenSocket();
	void onError(ErrorResponse errorResponse);
}
