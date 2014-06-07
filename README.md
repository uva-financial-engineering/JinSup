JinSup
======

This is a market simulator in development for the University of Virginia Financial Engineering Research Group.

To run the simulation, simply download `jinsup.jar` and run the following command in a terminal/command prompt.

```
java -jar jinsup.jar [options]
```

Options can be any of the following below. Options only need to be specified if the default value needs to be changed:
```
    --randomGenerator, -rng
       Random number generation algorithm
       Default: mersenne

    --seed
       PRNG seed
       Default: Number of milliseconds since Unix Epoch

    --destIAProfitFile, -dia
       File to write IA profits
       Default: IAProfits-YYYYMMDD-HHMM.csv

    --destTradeFile, -dt
       File to write trade log data
       Default: log-YYYYMMDD-HHMM.csv

    --config
       Path to the configuration file

    --help
       Show this usage information
       Default: false
```

See [Configuration File Formats](https://github.com/uva-financial-engineering/JinSup/wiki/Configuration-File-Formats) for more information about the configuration file. This parameter is required to run the simulation.

Also please see [Logging Columns](https://github.com/uva-financial-engineering/JinSup/wiki/Logging-Columns) for an explanation of what is logged in the simulation.

When test mode is disabled, the user will be able to see a moving history of the market condition in a graphing window as shown below.

![](https://raw.github.com/uva-financial-engineering/JinSup/master/tradeHistory.png)
