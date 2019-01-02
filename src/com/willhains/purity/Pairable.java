package com.willhains.purity;

public @Value interface Pairable<This extends Pairable<This>>
{
	default <@Value That> Pair<This, That> pairWith(final That that)
	{
		@SuppressWarnings("unchecked") final This self = (This)this;
		return Pair.of(self, that);
	}
}
