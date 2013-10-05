JBooks
======

This is a complex program to do a simple task: keep track of transactions, 
customers, and other data relating to grass cutting. I wrote it a long time
ago, but I decided to put it up here because I wanted to get used to using
git and GitHub.

Basically, the program is structured as follows:

+ TType
  - Defines the type of transactions that can take place, and how much they 
    default to.
+ EType
  - Works similar to above but with expenses.
+ Transaction
+ Expense
  - Defines a type, amount, and date to be stored.
+ Customer
+ ExpenseList
  - Composed of some metadata about the customer or list of expenses followed
    by a list of the corresponding elements.

