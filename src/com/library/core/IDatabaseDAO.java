package com.library.core;

import java.util.List;
import java.util.Optional;

public interface IDatabaseDAO {
    void saveItem(LibraryItem item);
    Optional<LibraryItem> findItemById(int itemId);
    List<LibraryItem> getAllItems();
    boolean isItemIdDuplicate(int itemId);
    void saveMember(Person member);
    Optional<Person> findMemberById(int memberId);
    List<Person> getAllMembers();
    boolean isMemberIdDuplicate(int memberId);
}