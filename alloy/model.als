sig GeographicalPosition{
}

sig Taxi{
	location: one GeographicalPosition
}

sig TaxiQueue{  // just a set of taxis
	taxi: set Taxi
}

sig TaxiZone{  // taxi-zone as defined in RASD
	queue: one TaxiQueue,
	positions: set GeographicalPosition
}
{
	#positions > 0 // an empty taxi zone would be useless
	all t: queue.taxi | t.location in positions  // if a taxi is the queue, then its location must belong to the zone as well
}

sig City{
	// note: the model itself does not impose that there is only one city
	// there could be many overlapping cities
	// the limitation #City = 1 is applied elsewhere
	taxis: set Taxi,
	zones: set TaxiZone,
	positions: set GeographicalPosition
}
{
	all z: TaxiZone | z.positions in positions  // taxi zones are made of the same positions which make up the city
	all z: TaxiZone | z.queue.taxi in taxis  // taxi zones contain only taxis which are in the city
	all t1, t2: TaxiZone | !(t1 = t2) => t1.positions & t2.positions = none  // taxi zones do not overlap
	all t1, t2: TaxiZone | !(t1 = t2) => t1.queue & t2.queue = none // different zones have different queues
}

// other important constraints
fact {
	all q: TaxiQueue | q in TaxiZone.queue  // every taxi-queue must belong to a taxi-zone
	all t: TaxiZone | t in City.zones  // every taxizone must belong to a city
	all p: GeographicalPosition | p in TaxiZone.positions  // every point in the city must belong to a taxi-zone (in other words: the city is fully covered by taxi-zones)
	// the following constraint is incorrect (commented away), since a taxi servicing a passenger resides in a zone, without appearing in the queue
	//all t: Taxi, z: TaxiZone | t.location in z.positions => t in z.queue.taxi  // the dual of what specified in TaxiZone's appended fact: if a taxi belongs to a zone's queue, then its location must be in the zone as well
	all q: TaxiQueue | q in TaxiZone.queue  // every taxiqueue must be attached to a taxizone
	all t: Taxi | t in City.taxis
	#City = 1  // the scope of our project
}

// arguments: 	t: target taxi, dest: its destination, fromZone, fromZone': pre and post state of the taxi's initial zone
//			toZone, toZone': pre and post state of the taxi's final zone
pred taxiMove[t: Taxi, start, dest: GeographicalPosition, fromZone, toZone, fromZone', toZone': TaxiZone] {
	start in fromZone.positions and start in fromZone'.positions
	dest in toZone.positions and dest in toZone'.positions
	fromZone'.queue.taxi = fromZone.queue.taxi - t
	toZone'.queue.taxi = toZone.queue.taxi + t
}

// helper predicates, used to describe the state of a taxi
pred taxiIsUnavailable[t: Taxi, z: TaxiZone] {
	t.location in z.positions
	t not in z.queue.taxi
}
pred taxiIsAvailable[t: Taxi, z: TaxiZone] {
	t.location in z.positions
	t in z.queue.taxi
}

// arguments: t: the target taxi, c: city in the pre-state, c': city in the post state
// we need a pre/post state city (and not just a pre/post state taxizone)
// because of vincula imposed on the taxi zones by city's append-fact
pred taxiBecomeUnavailable[t: Taxi, c, c': City] {  // TODO? unsure
	t in c.taxis and t in c'.taxis  // t always belongs to the city
	c.taxis = c'.taxis  // the taxi set is unchanged
	all z: c.zones | t not in z.queue.taxi => z in c'.zones  // all zones the same except the one with the changed taxi
	// the availability of t in the pre-state implies the unavailability of t in the post-state
	taxiIsAvailable[t, c.zones] => taxiIsUnavailable[t, c'.zones]
	c.positions = c'.positions  // the pre/post state are physycally the same city
}

// sister function of the above
pred taxiBecomeAvailable[t: Taxi, c, c': City] {
	t in c.taxis and t in c'.taxis  // t always belongs to the city
	c.taxis = c'.taxis  // the taxi set is unchanged
	all z: c.zones | t not in z.queue.taxi => z in c'.zones  // all zones the same except the one with the changed taxi
	// the availability of t in the pre-state implies the unavailability of t in the post-state
	taxiIsUnavailable[t, c.zones] => taxiIsAvailable[t, c'.zones] // changed from the other function
	c.positions = c'.positions  // the pre/post state are physycally the same city
}

pred show {
	#TaxiZone >= 2 // just to make things interesting
}
run show for 15
run taxiMove for 1 City, 1 Taxi, 2 GeographicalPosition, 5 TaxiZone, 5 TaxiQueue
run taxiIsUnavailable for 1 City, 4 Taxi, 4 GeographicalPosition, 4 TaxiZone, 4 TaxiQueue
run taxiIsAvailable for 1 City, 4 Taxi, 4 GeographicalPosition, 4 TaxiZone, 4 TaxiQueue
run taxiBecomeUnavailable for 1 City, 4 Taxi, 4 GeographicalPosition, 4 TaxiZone, 4 TaxiQueue
run taxiBecomeAvailable for 1 City, 4 Taxi, 4 GeographicalPosition, 4 TaxiZone, 4 TaxiQueue

