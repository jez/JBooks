@echo off
jar cvmf Notebook.mf Notebook.jar *.class zimmerman\jacob\notebook\*.class zimmerman\jacob\moneybook\*.class images\*.gif zimmerman\jacob\notebook\images\*.gif zimmerman\jacob\moneybook\images\*.gif
jar cvmf MoneyBookV10.mf "Money Book v10.jar" *.class zimmerman\jacob\notebook\*.class zimmerman\jacob\moneybookv10\*.class com\horstmann\corejava\*.class images\*.gif images\*.png zimmerman\jacob\notebook\images\*.gif zimmerman\jacob\moneybookv10\images\*.gif zimmerman\jacob\moneybookv10\images\*.png
pause