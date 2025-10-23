package com.library.core;

import java.util.List;
import java.util.Optional;

public class LibraryService {
    private final IDatabaseDAO dao;

    public LibraryService(IDatabaseDAO dao) {
        this.dao = dao;
    }

    public void addItem(LibraryItem item) throws Exception {
        if (dao.isItemIdDuplicate(item.getId())) {
            throw new Exception("Item ID " + item.getId() + " already exists.");
        }
        dao.saveItem(item);
    }
    public void addMember(Person member) throws Exception {
        if (dao.isMemberIdDuplicate(member.getId())) {
            throw new Exception("Member ID " + member.getId() + " already exists.");
        }
        dao.saveMember(member);
    }
    public void borrowItem(int memberId, int itemId) throws Exception {
        Optional<Person> memberOpt = dao.findMemberById(memberId);
        Optional<LibraryItem> itemOpt = dao.findItemById(itemId);
        if (!memberOpt.isPresent()) { throw new Exception("Member not found."); }
        if (!itemOpt.isPresent()) { throw new Exception("Item not found."); }
        LibraryItem item = itemOpt.get();
        if (item.isBorrowed()) { throw new Exception("Item is already borrowed."); }
        item.setBorrowed(true);
        dao.saveItem(item);
    }
    public void returnItem(int itemId) throws Exception {
        Optional<LibraryItem> itemOpt = dao.findItemById(itemId);
        if (!itemOpt.isPresent()) { throw new Exception("Item not found."); }
        LibraryItem item = itemOpt.get();
        if (!item.isBorrowed()) { throw new Exception("Item is already available."); }
        item.setBorrowed(false);
        dao.saveItem(item);
    }
    public boolean isItemIdDuplicate(int itemId) { return dao.isItemIdDuplicate(itemId); }
    public boolean isMemberIdDuplicate(int memberId) { return dao.isMemberIdDuplicate(memberId); }
    public List<LibraryItem> getAllItems() { return dao.getAllItems(); }
    public List<Person> getAllMembers() { return dao.getAllMembers(); }
}