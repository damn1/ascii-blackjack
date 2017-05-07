This is a small game I developed in Java as a lecture for the highschool I'm teaching in.
It's an implementation of the Blackjack game with a nice ascii interface (I will improve it), something like this:

d4mn, stack: 1000, do your bet: 560
================================================
	d4mn:
================================================
╓───╖╓───╖╓───╖╓───╖╓───╖╓──╖╓──╖
╣100╠╣100╠╣100╠╣100╠╣100╠╣50╠╣10╠
╙───╜╙───╜╙───╜╙───╜╙───╜╙──╜╙──╜
┌─────┌────────┐
│ 3 ♣ │ 5 ♣ · ·│
│ · · │ · · · ·│
│ · · │ · · · ·│
│ · · │ · · · ·│
│ · · │ · · 5 ♣│
└─────└────────┘
================================================
	Dealer:
================================================
┌─────┌────────┐
│ 8 ♣ │▓▓▓▓▓▓▓▓│
│ · · │▓▓▓▓▓▓▓▓│
│ · · │▓▓▓▓▓▓▓▓│
│ · · │▓▓▓▓▓▓▓▓│
│ · · │▓▓▓▓▓▓▓▓│
└─────└────────┘

Allows multiplayer. Does not allow split rule yet (is a TODO).
The game is developed in a structured programming paradigm, does not include use of classes different from Main one.
Moreover it does not include use of dynamic structures, since they're not part of the schedule of the classroom.

The lecture is about subroutines and algorithms design.
The aim is to 
  • understand the power of representations. 
  • understand how much important is to design an high level algorithm decomposing the problem into subroutines.
  • deeply understand to exploit the pass by reference of parameters to methods.
  • evaluate when and why to use global or local variables.
  
