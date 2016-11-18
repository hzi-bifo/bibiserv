#Dependency Parser

A parser written in JavaCC 4.0+ for describing dependencies if  bibiserv application function.


In some cases not all parameters of a function are needed at the same time or, in other cases, some parameters are exclusive to each other. Via dependencies, restrictions to the defined parameters of a function can be made using standard logical operators.

A dependency can only contain parameters that are actually defined in the function. References to parameters are set using the id of the parameter.

The definition below describes all possible interactions using standard BNF notation:

```
<Function>     ::= <AND> | <OR> | <XOR>| <NOT> | <IMPL> | <LOGEQ> | def(<id>) | <EQUALS> | <GREATER> | <GREATEREQUALS> | <LESSER> | <LESSEREQUALS>
<AND>          ::= and(<Function>,<Function>)        // conjunction
<OR>           ::= or(<Function>,<Function>)         // disjunction
<XOR>          ::= xor(<Function>,<Function>)        // exclusive disjunction
<NOT>          ::= not(<Function>)                   // denial
<IMPL>         ::= impl(<Function>,<Function>)       // implication
<LOGEQ>        ::= logeq(<Function>,<Function>)      // equivalence
<EQUALS>       ::= eq(<id>,<id> | <value>)
<GREATER>      ::= gt(<id>,<id> |<value>)
<GREATEREQUALS>::= ge(<id>,<id> |<value>)
<LESSER>       ::= lt(<id>,<id> |<value>)
<LESSEREQUALS> ::= le(<id>,<id> |<value>)
<id>           ::= @[A-Z,a-z,0-9]+
<value>        ::= [0-9]+[.]?[0-9]*|[A-Z,a-z,0-9,...]
```

See also the [BiBiServ Wiki](https://wiki.cebitec.uni-bielefeld.de/bibiserv-1.25.2/index.php/Dependency_Language_Dev) which may contain some additional informations.
