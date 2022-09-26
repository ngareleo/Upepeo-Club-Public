package Entities;

import Connections.Database;
import Errors.RegisteringIncompleteMember;
import Tools.Types;

import java.util.logging.Logger;

public class MemberHandler {

    private static Database database;
    private static Logger logger;
    private static final int REGISTRATION_FEE = 2000;

    public MemberHandler(Database database, Logger logger) {
        MemberHandler.database = database;
        MemberHandler.logger = logger;
    }

    public boolean registerNewMember(Member member) throws RegisteringIncompleteMember{
        if (!member.canBeRegistered()) throw new RegisteringIncompleteMember();
        Types.QueryProgress queryProgress = MemberHandler.database.addMember(member);
        if (queryProgress == Types.QueryProgress.ERROR) return false;
        queryProgress = MemberHandler.database.carryTransaction(REGISTRATION_FEE, Types.TransactionTypes.MEMBER_REGISTRATION);
        return queryProgress == Types.QueryProgress.ERROR;
    }

    public Member getMemberInformation(String membershipId){
        return MemberHandler.database.getMemberInformation(membershipId);
    }

    public boolean clearMemberBill(double totalBill, Member member) {
        Types.QueryProgress queryProgress = MemberHandler.database.carryTransaction(totalBill, Types.TransactionTypes.FINE_FEE);
        if (queryProgress != Types.QueryProgress.COMPLETE) return false;
        queryProgress = MemberHandler.database.payBill(member.getMembershipID());
        return queryProgress == Types.QueryProgress.COMPLETE;
    }
}
