# NeddlemanWunsch

An implementation of the standard Needleman-Wunsch algorithm just for fun.  Instead 
of the usual many-matrices approach, this implementation has an object-oriented 
twist:  There's just one matrix but every cell in that matrix is special NWCell 
object that accounts for both direction and score.
