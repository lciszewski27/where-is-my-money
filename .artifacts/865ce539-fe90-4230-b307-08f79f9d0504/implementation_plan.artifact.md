# Smoothness & Polish Implementation Plan

This plan outlines improvements to the UI/UX of the "Where is my money?" app to make it feel more fluid, responsive, and premium.

## Proposed Changes

The changes are grouped into five main areas: navigation transitions, list animations, micro-interactions, numerical transitions, and search fluidity.

---

### 1. Navigation & Shared Elements

We will implement **Shared Element Transitions** between the `DashboardScreen` and `PersonDetailScreen`. When a user taps a person card, their avatar and name will smoothly transition to their new position in the detail view.

#### [MODIFY] [AppNavHost.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/navigation/AppNavHost.kt)
- Wrap the `NavHost` content in `SharedTransitionLayout`.
- Pass the `SharedTransitionScope` and `AnimatedVisibilityScope` down to the screens.

#### [MODIFY] [DashboardScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/dashboard/DashboardScreen.kt)
- Apply `sharedElement` modifiers to the `PersonAvatar` and person name in the list.

#### [MODIFY] [PersonDetailScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/person/PersonDetailScreen.kt)
- Apply corresponding `sharedElement` modifiers to the header elements.

---

### 2. List & Content Animations

We will enhance the `LazyColumn` in the Dashboard to handle item additions, removals, and reordering with smooth animations.

#### [MODIFY] [DashboardScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/dashboard/DashboardScreen.kt)
- Add `Modifier.animateItem()` to the person cards in the `LazyColumn`.
- Adjust the entry animation for the list to be more staggered and graceful.

---

### 3. Numerical Polish

We will use the existing `AnimatedAmountText` component for all financial metrics to ensure that balance changes feel organic rather than jarring.

#### [MODIFY] [DashboardScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/dashboard/DashboardScreen.kt)
- Replace static text in `PersonCard` with `AnimatedAmountText`.
- Use `AnimatedAmountText` for metrics inside the `ExpandableBottomDrawer`.

---

### 4. Search Experience

The current search bar toggle is a hard swap of the TopAppBar. We will animate this transition.

#### [MODIFY] [DashboardScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/dashboard/DashboardScreen.kt)
- Use `AnimatedContent` for the transition between the normal `LargeTopAppBar` and the `SearchBar` mode.

---

### 5. Micro-interactions

We will add subtle responsiveness to touch inputs to provide better tactile feedback.

#### [MODIFY] [DashboardScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/dashboard/DashboardScreen.kt)
- Add a slight scale-down effect when pressing `PersonCard`.
- Ensure ripples are well-defined and respect card boundaries.

---

## Verification Plan

### Automated Tests
- Run existing UI tests to ensure no regressions in navigation logic.
- Verify `AnimatedAmountText` formatting with unit tests.

### Manual Verification
- Deploy to a device/emulator.
- Observe the avatar transition when clicking a person.
- Observe the number "rolling" effect when amounts change.
- Verify that searching for a person feels fluid and doesn't "blink" the UI.
