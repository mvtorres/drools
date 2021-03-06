package org.drools.compiler.phreak;

import java.beans.IntrospectionException;

import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.test.dsl.ReteTesterHelper;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.InternalReadAccessor;

public class BetaNodeBuilder {
    BuildContext buildContext;

    int          nodeType;
    
    Class        leftType;
    Class        rightType;
    String       leftFieldName;
    String       leftVariableName;

    String       constraintFieldName;
    String       constraintOperator;
    String       constraintVariableName;

    public BetaNodeBuilder(int nodeType, BuildContext buildContext) {
        this.nodeType = nodeType;
        this.buildContext = buildContext;
    }

    public static BetaNodeBuilder create(int nodeType, BuildContext buildContext) {
        return new BetaNodeBuilder( nodeType, buildContext );
    }

    public BetaNodeBuilder setLeftType(Class type) {
        this.leftType = type;
        return this;
    }

    public BetaNodeBuilder setRightType(Class type) {
        this.rightType = type;
        return this;
    }

    public BetaNodeBuilder setBinding(String leftFieldName,
                                      String leftVariableName) {
        this.leftFieldName = leftFieldName;
        this.leftVariableName = leftVariableName;
        return this;
    }

    public BetaNodeBuilder setConstraint(String constraintFieldName,
                                         String constraintOperator,
                                         String constraintVariableName) {
        this.constraintFieldName = constraintFieldName;
        this.constraintOperator = constraintOperator;
        this.constraintVariableName = constraintVariableName;
        return this;
    }

    public BetaNode build() {
        EntryPointNode epn = new EntryPointNode( buildContext.getNextId(),
                                                 buildContext.getRuleBase().getRete(),
                                                 buildContext );
        epn.attach( buildContext );

        ObjectTypeNode otn = new ObjectTypeNode( buildContext.getNextId(),
                                                 epn,
                                                 new ClassObjectType( leftType ),
                                                 buildContext );

        LeftInputAdapterNode leftInput = new LeftInputAdapterNode( buildContext.getNextId(),
                                                                   otn,
                                                                   buildContext );

        ObjectSource rightInput = new ObjectTypeNode( buildContext.getNextId(),
                                                      epn,
                                                      new ClassObjectType( rightType ),
                                                      buildContext );

        ReteTesterHelper reteTesterHelper = new ReteTesterHelper();

        Pattern pattern = new Pattern( 0, new ClassObjectType( leftType ) );

        //BetaNodeFieldConstraint betaConstraint = null;
        BetaConstraints betaConstraints = null;
        if ( constraintFieldName != null ) {
            ClassFieldAccessorStore store = (ClassFieldAccessorStore) reteTesterHelper.getStore();
    
            InternalReadAccessor extractor = store.getReader( leftType,
                                                              leftFieldName,
                                                              getClass().getClassLoader() );
    
            Declaration declr = new Declaration( leftVariableName,
                                                 extractor,
                                                 pattern );
            try {
                betaConstraints = new SingleBetaConstraints( reteTesterHelper.getBoundVariableConstraint( rightType,
                                                                              constraintFieldName,
                                                                              declr,
                                                                              constraintOperator ),  buildContext.getRuleBase().getConfiguration()  );
            } catch ( IntrospectionException e ) {
                throw new RuntimeException( e );
            }
        } else {
            betaConstraints = new EmptyBetaConstraints();
        }
        
        switch ( nodeType ) {
            case NodeTypeEnums.JoinNode:
                return new JoinNode( 0, leftInput, rightInput, betaConstraints, buildContext );
            case NodeTypeEnums.NotNode:
                return new NotNode( 0, leftInput, rightInput, betaConstraints, buildContext );
            case NodeTypeEnums.ExistsNode:
                return new ExistsNode( 0, leftInput, rightInput, betaConstraints, buildContext );                
        }
        throw new IllegalStateException( "Unable to build Node" );
    }

}
