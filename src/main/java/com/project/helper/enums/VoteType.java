package com.project.helper.enums;

    public enum VoteType {
        UPVOTE(1), DOWNVOTE(-1),
        ;
        private int direction;

        VoteType(int direction) {
        }

//    public static VoteType lookup(Integer direction) {
//        return Arrays.stream(VoteType.values())
//                .filter(value->value.getDirection().equals(direction))
//                .findAny()
//                .orElseThrow(()->new ResourceNotFoundException("Vote","Direction",String.valueOf(direction)));
//    }
}