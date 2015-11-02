sig GeographicalPosition{
}

sig Taxi{
	location: one GeographicalPosition
}

sig TaxiQueue{
	taxi: set Taxi
}

sig TaxiZone{
	queue: one TaxiQueue,
	positions: set GeographicalPosition
}
{
	#positions > 0 // an empty taxi zone would be useless
	all t: queue.taxi | t.location in positions
}

sig City{
	taxis: set Taxi,
	zones: set TaxiZone,
	positions: set GeographicalPosition
}
{
	all z: TaxiZone | z.positions in positions  // taxi zones are made of the same positions which make up the city
	all z: TaxiZone | z.queue.taxi in taxis
	all t1, t2: TaxiZone | !(t1 = t2) => t1.positions & t2.positions = none
	all t1, t2: TaxiZone | !(t1 = t2) => t1.queue & t2.queue = none
}

fact {
	all q: TaxiQueue | q in TaxiZone.queue
	all t: TaxiZone | t in City.zones
	all p: GeographicalPosition | p in TaxiZone.positions
	all t: Taxi, z: TaxiZone | t.location in z.positions => t in z.queue.taxi
	all q: TaxiQueue | q in TaxiZone.queue
}

pred show {
	#TaxiZone >= 2
}
run show for 15 but exactly 1 City
