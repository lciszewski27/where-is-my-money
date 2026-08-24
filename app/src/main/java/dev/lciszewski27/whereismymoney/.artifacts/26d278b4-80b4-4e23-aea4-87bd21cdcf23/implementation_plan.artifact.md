# Implementation Plan - Where Is My Money UI & Feature Enhancements

This plan outlines the steps to fix UI glitches, add new features (swipe actions, net balance display), improve the contact creation flow, fix search, and enhance the overall Material You 3 expressiveness.

## User Review Required

> [!IMPORTANT]
> The "Quick Action on Slide" will implement **Settle All** and **Delete** actions for contacts on the home screen.
> The "Person Net Balance" will display the total amount owed by/to each person in the primary currency.

## Proposed Changes

### Domain & Data Layer

#### [MODIFY] [Person.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/domain/model/Person.kt)
- Add `balanceCents: Long = 0L` and `currency: String = ""` fields to the `Person` data class to facilitate showing balances on the main list.

#### [MODIFY] [DebtRepository.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/domain/repository/DebtRepository.kt)
- Add `observePersonsWithBalance(primaryCurrency: String): Flow<List<Person>>` to the interface.

#### [MODIFY] [DebtRepositoryImpl.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/data/repository/DebtRepositoryImpl.kt)
- Implement `observePersonsWithBalance` by combining `personDao.observeAll()` and `debtDao.observeActive()`.
- Calculate the net balance for each person using `CurrencyConversionUseCase`.
- Fix the bug in `getActiveCurrencies`.

---

### Dashboard (Main Screen) Improvements

#### [MODIFY] [DashboardViewModel.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/dashboard/DashboardViewModel.kt)
- Update `observeData` to use `repository.observePersonsWithBalance` instead of `repository.observePersons`.
- Ensure search logic correctly filters the list of people with their balances.
- Add `DeletePerson` and `SettlePerson` events/logic for swipe actions.

#### [MODIFY] [DashboardScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/dashboard/DashboardScreen.kt)
- **Fix Filter Bar Glitch:** Remove manual `Icon` in `SegmentedButton` content and use the `icon` parameter properly to avoid double icons when selected.
- **Search Fix:** Implement a proper `SearchBar` or integrated `TextField` in the `DashboardTopAppBar`.
- **Net Balance Display:** Update `PersonCard` to show the person's net balance in the primary currency.
- **Swipe Actions:** Wrap `PersonCard` in a `SwipeToDismissBox` (or custom swipe component) to provide quick "Settle" and "Delete" actions.
- **Material 3 Expressiveness:**
    - Improve spacing and typography.
    - Use `AnimatedContent` for smoother transitions between filter states.
    - Enhance the `DashboardTopAppBar` with better visual hierarchy.

---

### Contact Creation & Profile Flow

#### [MODIFY] [AddDebtScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/adddebt/AddDebtScreen.kt)
- Refine the contact selection dropdown to make it more intuitive.
- Ensure that adding a new contact during debt creation feels like a seamless "first action".

#### [MODIFY] [PersonDetailScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/person/PersonDetailScreen.kt)
- Add an "Edit Profile" or "Contact Details" section where users can add more information (notes, etc.) as requested.
- Improve the visual design to be more "Material You 3 expressive" (e.g., using larger headers, better card styling).

---

### UI Components

#### [MODIFY] [PersonAvatar.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/components/PersonAvatar.kt)
- Add support for larger avatar sizes and potentially custom colors or images.

## Verification Plan

### Automated Tests
- Run existing unit tests for `DebtRepositoryImpl` and `DashboardViewModel`.
- Add new tests for `observePersonsWithBalance` logic.
- Verify `CurrencyConversionUseCase` remains accurate.

### Manual Verification
- **Search:** Verify that typing in the search bar filters the contact list in real-time.
- **Filter Bar:** Verify that switching between "All", "They Owe", and "I Owe" works smoothly without visual glitches.
- **Net Balance:** Verify that individual person balances are correctly calculated and displayed.
- **Swipe Actions:** Test swiping a contact to settle all their debts or delete the contact.
- **Material 3:** Verify that the app uses dynamic colors and has a modern, expressive feel on both light and dark modes.
