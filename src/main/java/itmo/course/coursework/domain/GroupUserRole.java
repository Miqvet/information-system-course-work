package itmo.course.coursework.domain;

public enum GroupUserRole {
    ADMIN,
    MEMBER;

    public static GroupUserRole findByName(String name) {
        for (GroupUserRole v : values())
            if (v.name().equals(name))
                return v;
        return null;
    }
}
