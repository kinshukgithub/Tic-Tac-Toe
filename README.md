# Tic Tac Toe

This is a Tic Tac Toe game that the user can play

**Name of the Project:** Dice Games App

**Name:** Kinshuk Goel\
**ID:** 2021A7PS2726G\
**Email:** f20212726@goa.bits-pilani.ac.in

**Name:** Hardaat Singh Baath\
**ID:** 2021A7PS2662G\
**Email:** f20212662@goa.bits-pilani.ac.in

## Description
This is a tic-tac-toe game that has a database login. The player has the option to play a single player game and double player game. For the single player, the player plays against the computer and for double player, the uer plays against someone else. The games care shown in the recently played games.

## Implementation
**Task 1 - Sign-in Screen Implementation**

- Initialized the Firebase object and incorporated user data insertion for seamless user registration.
- Enabled users to log in using any email and password, granting them access to a display of currently active games in the database.

**Task 2 - Single-Player Mode Integration**

- Facilitated the selection of a single-player game post-login.
- Implemented a mechanism where, during a single-player game, the computer autonomously selects a block after the player's turn.

**Task 3 - Two-Player Mode Development**

- Upon user login and game selection, dynamically created a new game entry in the database.
- Established a robust framework for two-player interactions, utilizing an array to track the progression of the tic-tac-toe grid.

## Testing
For testing, we have used TDD. Test-Driven Development (TDD) is a software development approach where you write tests before writing the actual code. You start by creating a test case that defines the expected behavior of a specific piece of functionality. Then, you write just enough code to make that test pass. TDD promotes code quality, rapid feedback, and helps prevent regressions in your software as it evolves. It's a valuable practice for ensuring the reliability and robustness of your code, especially in the field of Software Development, where precision and correctness are crucial. We also tested unit test cases.

## Accessibility
While using the Accessibility Scanner, we faced the following errors:
1. Text Scaling: This was an error caused due to the size of the heading.
2. Text Contrast: The forground to background contrast ratio was not in an acceptable range.

### Talkback Experience
During the accessibility testing, we tried to run the app using only the talkback feature of android. The feature worked properly, and we were able to use the app with the eyes closed. One possible problem that we faced was the changing layout in portrait and landscape mode, which might be a problem for the visually impared when using for the first time. 

**Hours taken to complete the assignment:** Approximately 25-30 hours spread across the entire time duration between the release of 
the assignment and the deadline.

**Pair Programming Rating:** 5(Both the team members actively involved in the development of this application)

**Difficulty-wise Rating:** 10/10