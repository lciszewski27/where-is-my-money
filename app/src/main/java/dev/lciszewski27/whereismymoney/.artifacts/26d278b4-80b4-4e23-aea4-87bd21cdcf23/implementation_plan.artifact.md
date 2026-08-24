# Implementation Plan - Person Editing & UX Polishing

This plan addresses person editing, transaction history editing, and critical layout fixes for the top bar and search.

## Proposed Changes

### Dashboard Enhancements

#### [MODIFY] [DashboardScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/dashboard/DashboardScreen.kt)
- **Top Bar Fix**: Switch to `TopAppBar` (standard height) for better stability when search is active.
- **Search Integration**: Overlay the search bar properly to prevent "glitching" with the app title.
- **Visibility**: Ensure the "Where is my money?" text is fully visible.
- **Centering**: Ensure empty state content is perfectly centered vertically and horizontally.

### Person Detail & Editing

#### [MODIFY] [PersonDetailScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/person/PersonDetailScreen.kt)
- **Edit Person Dialog**: Add a dialog to change the person's name and pick a new color seed for their avatar.
- **Edit Transactions**: Add an "Edit" action to each transaction in the history list.
- **Delete Navigation**: Verify and ensure the user is popped back to the dashboard immediately after person deletion.

#### [MODIFY] [PersonDetailViewModel.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/person/PersonDetailViewModel.kt)
- **Update Person Logic**: Implement `UpdatePerson` event to save changes to the person's name and color.
- **Navigation Events**: Add an explicit `navigateToEditDebt` flow if needed (or reuse existing bottom sheet logic).

#### [MODIFY] [PersonDetailUiState.kt](file:///home/lciszewski27/whereismymoney/ui/person/PersonDetailUiState.kt)
- Add `UpdatePerson(name: String, colorSeed: Long)` event.
- Add `EditDebt(debtId: String)` event.

### Navigation Fixes

#### [MODIFY] [AppNavHost.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/navigation/AppNavHost.kt)
- Ensure the `AddDebtSheet` can be triggered from the `PersonDetailScreen` for editing existing debts.

## Verification Plan

### Manual Verification
- **Edit Person**: Change a name and color, verify it updates on both detail and dashboard screens.
- **Edit Transaction**: Tap edit on a history item, change amount, and verify balance updates.
- **Search**: Toggle search on the dashboard; verify the title disappears and search bar appears smoothly without jumping.
- **Empty State**: Delete all people and verify the empty state is perfectly centered.
