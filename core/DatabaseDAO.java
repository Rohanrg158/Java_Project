package com.library.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseDAO implements IDatabaseDAO {
    private final List<LibraryItem> items = new ArrayList<>();
    private final List<Person> members = new ArrayList<>();

    @Override
    public void saveItem(LibraryItem item) { 
        items.removeIf(i -> i.getId() == item.getId());
        items.add(item); 
    }
    @Override
    public Optional<LibraryItem> findItemById(int itemId) {
        return items.stream().filter(i -> i.getId() == itemId).findFirst();
    }
    @Override
    public List<LibraryItem> getAllItems() { return items; }
    @Override
    public boolean isItemIdDuplicate(int itemId) {
        return items.stream().anyMatch(i -> i.getId() == itemId);
    }
    @Override
    public void saveMember(Person member) {
        members.removeIf(m -> m.getId() == member.getId());
        members.add(member);
    }
    @Override
    public Optional<Person> findMemberById(int memberId) {
        return members.stream().filter(m -> m.getId() == memberId).findFirst();
    }
    @Override
    public List<Person> getAllMembers() { return members; }
    @Override
    public boolean isMemberIdDuplicate(int memberId) {
        return members.stream().anyMatch(m -> m.getId() == memberId);
    }
}